# Proposal — PBI 1095186: Envío de boletas múltiples (Falabella / Fluxo SS)

> **Work Item:** [#1095186](https://db1global.visualstudio.com/_workitems/edit/1095186)
> **Feature padre:** [#1129859](https://db1global.visualstudio.com/_workitems/edit/1129859) — "[Forus][Falabella] Permitir dividir 100 pedidos e etiquetas"
> **País:** Chile (Forus) · **Story Points:** 13 · **Prioridad:** 1
> **Desarrollador:** Juan Pablo Orphanopoulos Capdeville

---

## 1. Contexto de negocio

Cuando un pedido de Falabella es dividido en múltiples paquetes/etiquetas de despacho (flujo de reembalaje ya soportado por este connector), Any'Core termina teniendo **una nota fiscal (NF/boleta) por paquete**, no una sola NF para todo el pedido. El módulo Falabella debe identificar qué documento fiscal corresponde a qué paquete y enviar a Falabella **múltiples boletas** (una llamada `SetInvoicePDF` por paquete), en vez de la única boleta a nivel de pedido completo que soporta el flujo clásico.

Este PBI es el eslabón "**Fluxo SS**" (Split Shipping): aplica cuando la división de etiquetas **ya fue informada** (no crea el split, lo consume).

## 2. Qué ya existe hoy (hallazgo clave de la investigación)

El PBI hermano **#1100204** ("Consumir notificación interna para multi etiquetas") ya está mergeado en esta misma branch y construyó la infraestructura base:

- `PackageFalabellaAPI` (`POST /api/remote/public/orders/package`) recibe la notificación interna de paquetes → `SendPackageConsumer` (JMS) → `PackageOrderService.handleEvent(...)`.
- Evento `UPDATE_INVOICED_PACKAGE` → `PackageOrderService.handleUpdateInvoicedPackage(...)` **ya envía una boleta por paquete**: por cada `SendPackageItem` con `invoice` no vacío, arma un `FalabellaInvoiceDTO` (`PackageTranslator.buildInvoiceDTO`) y llama `DocumentService.sendInvoice(...)` — una llamada real a Falabella por paquete.
- `invoiceNumber` e `invoiceDate` **ya se rescatan directamente del `SendPackageInvoice`** de la notificación (no hace falta ir a buscar metadata del pedido en AnyMarket, a diferencia del flujo clásico de boleta única que sí depende de `INVOICE_NUMBER`/`INVOICE_DATE` en la metadata del pedido).

Es decir: **el "camino feliz" del envío múltiple ya existe**. Este PBI no construye ese camino desde cero — cierra tres huecos concretos detectados al auditar `PackageOrderService.handleUpdateInvoicedPackage` contra los criterios de aceite del PBI.

## 3. Huecos a cerrar (alcance real de este PBI)

> El equipo ya hizo una pré-análise y creó 6 tasks hijas en ADO (#2137392, #2137394, #2137395, #2137396, #2137398, #2137399 — ver [task.md](./task.md)). Los huecos 3.1-3.3 de abajo se corresponden 1:1 con las Tasks 1, 2 y 3 de ese desglose, más una pieza adicional (idempotencia) acordada en el análisis de este documento que no estaba en las 6 tasks originales.
>
> **Actualización post-reunión de discovery:** la pieza "confirmación a AnyMarket" (§3.2 más abajo) que se había sumado en la primera vuelta de este análisis fue **removida del alcance** — quedó bloqueada por una limitación real del SDK. Ver §7 para el detalle completo de esta reconciliación.

### 3.0 `orderItemIds` mal armado (Task 1, #2137392 — bug detectado en pré-análise)
`PackageTranslator.buildInvoiceDTO` arma hoy `orderItemIds = List.of(packageItem.getPackageIdItem())` — es decir, manda el **identificador del paquete** como si fuera un ID de ítem de pedido. La API `SetInvoicePDF` de Falabella espera los `OrderItemId` reales de los ítems que van en ese paquete. El dato correcto existe (`FalabellaOrderItem` trae tanto `OrderItemId` como `PackageId` por ítem, ya usado en el flujo `ReadyToShip`) — falta filtrar esa lista por el paquete en cuestión.

### 3.1 Documento del PDF en Base64 (falta construir)
`PackageTranslator.buildInvoiceDTO` hoy asigna `invoiceDocument = invoice.getInvoiceUrl()` — una **URL**, no el PDF codificado en Base64 que exige el contrato real de Falabella (`invoiceDocument: "<PDF_en_Base64>"`). El flujo clásico (`FalabellaInvoiceTranslator`) no tiene este problema porque recibe el Base64 ya resuelto en el mensaje JMS. Para el flujo por paquete no existe ningún mecanismo de descarga: hay que agregarlo (descargar el PDF desde `invoiceUrl` y codificarlo en Base64 antes de armar el DTO).

### 3.2 Confirmación de resultado a AnyMarket — REMOVIDO DEL ALCANCE (bloqueado por el SDK, ver §7)
`handleUpdateInvoicedPackage` llama a `documentService.sendInvoice(...)` y no reporta nada de vuelta a AnyMarket — ni éxito ni error. El flujo clásico sí lo hace (`BusinessOrderInvoiceService.sendInvoiceToOrder` llama a `middlewareOrderService.updateAnymarketOrderTransmission(...)` con `OrderStatusUpdate.withSuccess()/.withError(...)`).

Se había planteado replicar ese patrón por paquete, condicionado a que la confirmación viajara acompañada del `packageId` correspondiente (si no, Nico señaló en la reunión de discovery que "no tiene efecto" para Core). Se verificó decompilando el SDK `anymarket-marketplace-sdk-api` que **`OrderStatusUpdate.withSuccess()` no admite ningún parámetro** — no hay forma de adjuntar `packageId` en una confirmación de éxito con la versión actual (1.47.100) ni con la más nueva disponible localmente (1.47.127). Es una limitación real de la librería, no un detalle de implementación de este repo. **Se decidió sacar esta pieza del alcance del PBI** — ver §7.

### 3.3 Manejo de error e idempotencia por paquete (Task 3, #2137395 — "In Progress")
Hoy el `for` sobre los paquetes no tiene try/catch individual: si el envío del paquete 2 de 3 falla, la excepción se propaga y corta el procesamiento de los paquetes restantes. La Task 3 exige que errores **permanentes/de negocio** de Falabella (boleta duplicada, item en status inválido — lo que el PBI llama "E004") **no generen reintento**, mientras que errores **transitorios** (5xx, timeout) sí deben seguir su curso normal de retry a nivel de consumer JMS (`AbstractRetryableConsumer`).

Esto se resuelve por **tipo de excepción**, no parseando código de error: `MarketplaceAccountException` (lo que ya lanza `DocumentService.sendInvoice` ante un `ErrorResponse` de Falabella en un 200 OK) se captura y traga por paquete; cualquier otra excepción (`MarketplaceUnavailableException` por 5xx, fallos de red, etc.) se deja propagar. Ver detalle en [design.md](./design.md) §1 y §4.

Como consecuencia, cuando SÍ ocurre un reintento de mensaje completo (por un error transitorio), hace falta **idempotencia**: no existe hoy ninguna columna ni marca persistida de "boleta ya enviada con éxito para este paquete" (`FalabellaPackage`/`FalabellaPackageItem` no tienen ese campo — solo hay un `PACKAGE_DATA` con el JSON crudo de la notificación), por lo que un reintento de mensaje re-enviaría de nuevo la boleta de paquetes ya facturados con éxito.

## 4. Fuera de alcance

- **Agregar `packageId` al endpoint compartido `OrderRemoteAPI` (`PUT /order/{id}/invoice/document[...]`)** — hipótesis planteada en vivo por Pablo en la reunión de discovery, no cerrada por el equipo. Verificado en código: ese endpoint no tiene `packageId` hoy, y su traductor (`FalabellaInvoiceTranslator`) no tiene ninguna lógica de filtrado por paquete — soportarlo requeriría trabajo real, no solo agregar un campo. **Se mantiene fuera de alcance de este PBI** por decisión explícita, no porque ya esté resuelto — es un riesgo abierto documentado en §7, pendiente de cierre con Nico/Pablo.
- Cambios al flujo clásico de boleta única (`BusinessOrderInvoiceService`/`FalabellaInvoiceTranslator`) — se usa solo como referencia de patrón, no se modifica.
- Creación/edición del split de etiquetas en sí (`handleCreatedPackage`, `handleUpdateShippedPackage`, `handleUpdateDeliveredPackage`) — fuera de este PBI.
- Confirmación de éxito/error a AnyMarket por paquete (`updateAnymarketOrderTransmission`) — ver §3.2 y §7: bloqueada por una limitación del SDK, removida del alcance.

## 5. Dependencias

| PBI | Título | Estado |
|---|---|---|
| [#1100204](https://db1global.visualstudio.com/_workitems/edit/1100204) | Consumir notificación interna para multi etiquetas | Avaliação da entrega (mergeado en esta branch) |
| [#1100721](https://db1global.visualstudio.com/_workitems/edit/1100721) | PUT fiscalDocument con packageId (lado Any'Core) | Done |

## 6. Valor de negocio

Sin este cierre, un pedido dividido en N paquetes queda con boletas incompletas o inconsistentes ante Falabella (documento inválido por no ser Base64, riesgo de reenvío duplicado). El cliente Forus (Chile) opera activamente este escenario de múltiples etiquetas/boletas por bulto.

## 7. Reconciliación post-reunión de discovery (verificado en código real)

En la reunión de discovery de hoy surgieron dos puntos sin cerrar que exigieron volver al código antes de tocar nada. Resumen de lo confirmado:

### 7.1 Hipótesis de Pablo — ¿el disparador es el endpoint `fiscalDocument` compartido, no (solo) el evento JMS?

Pablo planteó en vivo que el mismo endpoint que hoy usa el flujo clásico de boleta única (`OrderRemoteAPI.sendInvoice`, `PUT /order/{id}/invoice/document`) podría ser el que le llega al módulo con la boleta a mandar a Falabella, y que si esa llamada ahora trae un `packageId`, habría que enrutar por ahí en vez de asumir que el único disparador es el evento JMS `UPDATE_INVOICED_PACKAGE` de #1100204.

**Verificado en código:**
- `OrderRemoteAPI.sendInvoice`/`sendManualInvoice` son endpoints entrantes (AnyMarket Core llama hacia este conector). **No tienen `packageId` hoy**, ni en el `@RequestBody` ni en los DTOs (`SendInvoiceOrderMessage`, `RemoteApiOrderInvoiceManual`) — se leyeron completos, sin campos genéricos ni placeholders.
- Agregar el campo sería trivial (POJOs Jackson simples, sin romper compatibilidad). **Usarlo no lo es**: `FalabellaInvoiceTranslator.generateInvoiceDTO` arma `orderItemIds` con **todos** los ítems del pedido, sin ningún filtro por paquete — esa lógica de filtrado no existe y habría que construirla desde cero.
- El evento JMS (`PackageOrderService.handleUpdateInvoicedPackage`, #1100204) en cambio **ya funciona de punta a punta hoy** — es el camino con menos trabajo pendiente.
- Nico no cerró en la reunión cuál es "el" disparador correcto — puede que el equipo termine necesitando ambos.

**Decisión (confirmada con el usuario):** este PBI se diseña e implementa sobre el evento JMS como único disparador. El endpoint `fiscalDocument` compartido queda explícitamente fuera de alcance (§4), documentado como riesgo abierto — no como algo descartado. Si en el futuro Core efectivamente empieza a llamar ese endpoint con `packageId`, la idempotencia ya diseñada (`FalabellaPackageService.isInvoiceAlreadySent`/`markInvoiceSent`, clave `packageIdItem`+`orderId`+`invoiceNumber`) es agnóstica del disparador y ya cubriría el riesgo de doble envío entre ambos caminos.

### 7.2 Hipótesis de Pablo sobre la tabla local de packageId ↔ order items — no se confirma

Pablo planteó que ya existe una tabla local (guardada en el momento del split) que cruza `packageId` con los ítems del pedido, para no tener que volver a pedirle el listado a Falabella, y que la Task 1 (#2137392) debería reusarla en vez del filtrado sobre `FalabellaOrderItem` propuesto originalmente.

**Verificado en código:** la tabla existe (`FalabellaPackage`/`FalabellaPackageItem`), pero **solo persiste `sku` + `quantity`** — exactamente lo que trae el webhook de split de Falabella (`SendPackageSkuItem`), que nunca incluye `OrderItemId`. Tampoco se captura la respuesta de `orderService.repackage(...)` (la llamada que crea el split en Falabella), que se descarta sin leer. **No existe en ningún lado del repo una tabla con `OrderItemId` real asociado a `packageId`.** Resolver `OrderItemId` sigue requiriendo una llamada en vivo a Falabella (`GetOrderItems`), correlacionada por SKU normalizado (no por `packageId`, cuya igualdad entre el webhook y la respuesta de Falabella no está verificada en el código). Ver design.md §2.1 para el detalle.

> **✅ Cerrado (02/09).** Se había reabierto esta duda en un follow-up (JP volvió a afirmar que la tabla alcanzaba, en línea con lo que decía Pablo en la reunión). Se resolvió durante la implementación real: `FalabellaPackageItem.java` (líneas 27-40) confirma que la entidad solo tiene `primaryKey` (`packageIdItem`+`orderId`), `shopSku`, `sku` y `quantity` — sin `orderItemId` ni equivalente. El hallazgo original de este párrafo era correcto. Task 1 se implementó con la llamada en vivo a `GetOrderItems`, sin cambios respecto al diseño original.

### 7.3 Pedido de Nico — confirmación a AnyMarket con `packageId`, o "no tiene efecto"

Nico señaló en la reunión que una confirmación de status a AnyMarket sin el `packageId` correspondiente "no tiene efecto" — sin cerrar si eso bloqueaba la pieza o no.

**Verificado en código (decompilando el SDK):** `OrderStatusUpdate.withSuccess()` (usado por el flujo clásico vía `BusinessOrderInvoiceService`) **no admite ningún parámetro** — ni `packageId` ni ningún otro dato adicional — en la versión declarada en `pom.xml` (1.47.100) ni en la más nueva disponible en el repositorio Maven local (1.47.127, misma estructura). Solo el camino de error (`withError(OrderMessage)`) tiene un `Map<String,Object> parameters` donde técnicamente cabría, pero no hay equivalente para el camino de éxito. Confirma literalmente la observación de Nico: es una limitación real del SDK, no un detalle pendiente de este repo.

**Decisión (confirmada con el usuario):** se remueve esta pieza del alcance del PBI (§3.2, §4). Si se necesita en el futuro, requiere gestionar con el equipo dueño de `anymarket-marketplace-sdk-api` un cambio de SDK — fuera del control de este repositorio.

### 7.4 Riesgo nuevo identificado en la reunión — asincronía del repackage hacia Falabella

Pablo señaló (conocimiento tácito del equipo, no verificado en código en esta sesión) que el repackage hacia Falabella es **asíncrono**: tras dividir un pedido en paquetes, el `packageId` de cada ítem puede tardar unos **~5 minutos** en reflejarse correctamente al reconsultar la orden — Falabella no responde de forma síncrona con el resultado final del split. Nico confirmó que ese caso puntual ya se está atendiendo en un PBI paralelo (a cargo de Marcelo) con polling proactivo del tracking code.

**Riesgo para este PBI, sin task asignada:** si el evento `UPDATE_INVOICED_PACKAGE` llega y dispara `handleUpdateInvoicedPackage` antes de que ese reflejo asíncrono haya terminado, `resolveOrderItemIds` (Task 1) podría no encontrar match para el paquete todavía. No se decidió en la reunión si esto necesita manejo explícito (ej. tratar "sin match" como reintentable en vez de descartar directo) o si en la práctica el evento siempre llega después de que el split ya está resuelto. **Pendiente de confirmar con Marcelo/Nico** antes de cerrar el comportamiento de Task 1.3 (hoy: log + `continue`, sin retry).

**Matizado durante la implementación (02/09), no implementado ningún manejo nuevo:** `resolveOrderItemIds` correlaciona por SKU normalizado, no por el `PackageId` que devuelve Falabella — y el SKU de un ítem no cambia porque el pedido se divida en paquetes, solo cambia su agrupación. Esto sugiere que el retraso de ~5 min en reflejar el `packageId` no debería afectar directamente esta resolución. Queda sin verificar empíricamente si el listado completo de `FalabellaOrderItem` (todos los SKUs) está siempre disponible de inmediato tras el split, o si hay alguna ventana donde falte algún ítem — no se pudo probar contra un split real en curso. Task 1.3 se implementó tal cual estaba diseñada (log + `continue`, sin retry).

## 8. Preguntas del discovery — estado final (02/09, tras revisar el código real en la implementación)

La reunión de discovery se apartó del guion de preguntas preparado y se cortó a los ~41 minutos por otro compromiso del equipo. Estado final de cada una, actualizado con lo que surgió al tocar el código real durante la implementación:

1. **¿Algún otro flujo de Falabella ya distingue error de negocio (duplicado vs. inválido) que sirva de referencia para el E004?** No se encontró ningún parseo explícito de "E004" en el repo (`grep -rni "E004"` sin resultados). Sí hay un precedente de patrón: `BusinessOrderService.getFalabellaOrdersWithoutItems` (líneas 153-163) ya captura `MarketplaceAccountException`/`MarketplaceContentProcessingException` dentro de un `for`, loguea, marca como fallido y continúa — mismo patrón usado en Task 3, aplicado antes a otro dominio (creación de pedidos). No resuelve la pregunta original, pero confirma que el enfoque elegido no es ad-hoc.
2. **¿Se vio en producción un paquete real de Forus con más de un documento de factura?** (Task 4, #2137396) **Sigue sin responder — y la Task 4 completa quedó sin implementar** (ver task.md). No hay evidencia en código ni en tests existentes de que esto ocurra; es la pregunta que la propia Task 4 estaba pensada para resolver.
3. **¿Bugs históricos de envío de facturas a marketplaces a cubrir con test en este cambio?** No se encontró ninguno directamente aplicable (el único hallado, sobre `INVOICE_DATE` nulo en el flujo clásico, no aplica al flujo nuevo porque no parsea strings de metadata). Pero surgió un **gap de test nuevo, no anticipado**: no hay cobertura para `invoice.getInvoiceDate() == null` en el flujo por paquete — agregar como escenario 10 de test.md.
4. **Con las 6 tasks ya desglosadas, ¿alguien ve complejidad oculta que el desglose no capture?** No se discutió por ID de task en la reunión — pero de la conversación libre surgieron los riesgos ya cerrados/matizados de §7.2 (tabla local, cerrado) y §7.4 (asincronía del repackage, matizado pero no verificado empíricamente).

**La pregunta *"¿hace falta reconsultar la NF después del envío, o alcanza con usarla en el momento del evento?"* sí se respondió**: alcanza con usarla en el momento del evento — el `invoiceNumber`/`invoiceDate` ya vienen en el payload de la notificación (`SendPackageInvoice`), sin necesidad de reconsultar nada después. Ver §2 arriba.
