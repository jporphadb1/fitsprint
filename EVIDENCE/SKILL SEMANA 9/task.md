# Task — PBI 1095186: Envío de boletas múltiples (Falabella / Fluxo SS)

> Este PBI ya tiene 6 tasks hijas creadas en ADO por una pré-análise previa. Esta lista **no las reemplaza**: detalla los pasos atómicos de implementación dentro de cada una, en el orden real de ejecución, e incorpora 1 pieza nueva (idempotencia) acordada en este análisis que no estaba en el desglose original — ver nota al final.
>
> **Actualización post-reunión de discovery (ver proposal.md §7 / design.md §5, §2.8):**
> - Se mantiene el evento JMS `UPDATE_INVOICED_PACKAGE` como único disparador — no se toca el endpoint `OrderRemoteAPI`/`fiscalDocument` en ninguna task.
> - La correlación `packageId → orderItemId` (Task 1) es **por SKU**, vía llamada en vivo a Falabella — el código investigado en la reunión mostró que la tabla local (`FalabellaPackage`/`FalabellaPackageItem`) solo persiste `sku`+`quantity`, sin `OrderItemId`.
> - **✅ Cerrado (02/09, verificado durante la implementación real):** se había reabierto la duda de si la tabla local alcanzaba (JP la había vuelto a plantear en un follow-up). La implementación real confirmó, leyendo `FalabellaPackageItem.java` líneas 27-40, que la entidad solo tiene `primaryKey` (`packageIdItem`+`orderId`), `shopSku`, `sku` y `quantity` — **no existe `orderItemId` ni equivalente**. Confirma el hallazgo original de proposal.md §7.2. Se mantiene la llamada en vivo (`getFalabellaOrderItems`), sin cambios respecto al diseño original.
> - Se **eliminó** la Task nueva B (confirmación a AnyMarket) de esta lista — bloqueada por el SDK (`OrderStatusUpdate.withSuccess()` no admite `packageId`).
> - **Riesgo de asincronía del repackage — matizado (02/09):** verificado durante la implementación que `resolveOrderItemIds` correlaciona por SKU, no por `PackageId` de Falabella, y el SKU no cambia por la división en paquetes — el retraso de ~5 min en reflejar el `packageId` no debería afectar directamente esta resolución. **Sigue sin verificar empíricamente** si el listado completo de `FalabellaOrderItem` está siempre disponible de inmediato tras el split, o si hay alguna ventana donde falte algún ítem — no se pudo probar contra un split real en curso. Sin task asignada todavía; confirmar con Marcelo/Nico.
> - **⚠️ Task 4 (#2137396) — NO implementada, sigue completamente abierta.** La implementación de las Tasks 1/2/3/5/6 (ADO) + Task A (idempotencia) no incluyó la Task 4: el código sigue con `packageItem.getInvoice().get(0)` (`PackageOrderService.java` línea 225) sin el comentario de decisión que pedía 4.2, porque 4.1 (confirmar con el equipo si el escenario de múltiples invoices por paquete es real) nunca se respondió. **No dar esta task por completa en el code review.**
> - **Gap de test nuevo, encontrado durante la implementación (no estaba en los 9 escenarios de test.md):** no hay cobertura para `invoice.getInvoiceDate() == null` en el flujo nuevo. No se replica el bug histórico del flujo clásico (que parseaba un string de metadata), pero tampoco se verificó qué pasa si Falabella/Any'Core mandan `invoiceDate` nulo en la notificación — el DTO lo serializaría como `null` vía `@JsonFormat`, comportamiento de la API de Falabella ante eso sin confirmar. Agregar como escenario 10 de test.md antes o durante el code review.
> - **Preguntas del deck de discovery sin responder en la reunión** (se cortó a los ~41 min): flujo de referencia explícito para E004 — no se encontró ninguno, pero sí un patrón ya establecido en el repo de "catch específico + log + continue dentro de un for" (`BusinessOrderService.getFalabellaOrdersWithoutItems`, líneas 153-163) que respalda el enfoque usado; paquete real de Forus con >1 factura — sigue sin responder, es justamente lo que decidiría la Task 4 de arriba; bugs históricos de envío de facturas — no se encontró ninguno aplicable directamente (ver gap de test nuevo arriba en su lugar). Ver proposal.md §8.
> - **Sin commits ni push todavía.** Todo el trabajo está como cambios locales sin confirmar en `feature/1095186_envio_boletas_multiples` (branch preexistente). No hay PR.

---

## Task 1 — [#2137392] Corregir `orderItemIds` en `PackageTranslator.buildInvoiceDTO`

- [x] 1.0 **Cerrado (02/09):** confirmado contra el código real (`FalabellaPackageItem.java` líneas 27-40) que la tabla local NO tiene `orderItemId`/equivalente — solo `primaryKey`, `shopSku`, `sku`, `quantity`. 1.1/1.2 quedan como estaban diseñadas, sin cambios.
- [x] 1.1 (implementado, `PackageOrderService.resolveOrderItemIds`, líneas 149-165) filtra `falabellaOrderItems` por **SKU normalizado** de `packageItem.getItems()` y devuelve los `OrderItemId` reales — **no** correlaciona comparando `packageIdItem` contra `FalabellaOrderItem.getPackageId()`. Nota de implementación: `FalabellaOrderItem.getOrderItemId()` devuelve `Long`, se mapea a `String` porque `FalabellaInvoiceDTO.orderItemIds` es `List<String>`.
- [x] 1.2 (implementado, línea 197) obtiene `falabellaOrderItems` vía `getFalabellaOrderItems(sellerConfiguration, orderId)` — llamada en vivo confirmada como necesaria por 1.0 — y llama a `resolveOrderItemIds(...)` antes de armar el DTO.
- [ ] 1.3 Si `resolveOrderItemIds` devuelve lista vacía: loguear error con contexto (`OI`, `ORDER`, `packageIdItem`) y `continue` al siguiente paquete — no llamar a Falabella.
- [ ] 1.4 Cambiar la firma de `PackageTranslator.buildInvoiceDTO(...)` para recibir `List<String> orderItemIds` en vez de derivarlo internamente de `packageItem.getPackageIdItem()`.
- [ ] 1.5 Test: `PackageOrderServiceTest` — paquete con múltiples ítems resuelve todos los `OrderItemId` correctos; paquete sin match resuelve lista vacía, loguea y no rompe el for.
- [ ] 1.6 Test: `PackageTranslatorTest` — `buildInvoiceDTO` con `orderItemIds` explícito arma el DTO correctamente (ya no usa `packageIdItem`).

## Task 2 — [#2137394] PDF en Base64 en vez de URL

- [ ] 2.1 Crear `src/main/java/br/com/anymarket/marketplace/integration/marketplace/document/service/InvoiceDocumentDownloadService.java`, inyectando el bean `RestTemplate` existente (`ApplicationConfig.restTemplate`).
- [ ] 2.2 Implementar `String downloadAndEncode(String invoiceUrl)`: `restTemplate.getForObject(invoiceUrl, byte[].class)` → validar no nulo/vacío → `Base64.getEncoder().encodeToString(bytes)`.
- [ ] 2.3 Si la descarga falla o el body viene vacío, lanzar una excepción que **no** sea `MarketplaceAccountException` (para que se trate como error transitorio y se propague — ver Task 3). Usar/crear una excepción existente del proyecto que ya se comporte así (revisar `MarketplaceContentProcessingException`/`MarketplaceUnavailableException` antes de crear una nueva).
- [ ] 2.4 En `PackageOrderService.handleUpdateInvoicedPackage`, llamar a `invoiceDocumentDownloadService.downloadAndEncode(invoice.getInvoiceUrl())` antes de armar el DTO, e inyectar el resultado en `packageTranslator.buildInvoiceDTO(...)`.
- [ ] 2.5 Actualizar `PackageTranslator.buildInvoiceDTO(...)` para recibir el Base64 ya calculado como parámetro (`invoiceDocumentBase64`) en vez de leer `invoice.getInvoiceUrl()` internamente.
- [ ] 2.6 Test nuevo: `InvoiceDocumentDownloadServiceTest` — descarga OK, `RestTemplate` devuelve null/vacío, `RestTemplate` lanza excepción de red.
- [ ] 2.7 Test: `PackageTranslatorTest` — `invoiceDocument` del DTO resultante es el Base64 recibido, no una URL.

## Task 3 — [#2137395, "In Progress"] Errores permanentes (E004) sin retry, transitorios sí

- [ ] 3.1 Confirmar que `DocumentService.sendInvoice` sigue lanzando `MarketplaceAccountException` para cualquier `ErrorResponse` de Falabella en un 200 OK (ya es el caso hoy — no requiere cambio en `DocumentService`).
- [ ] 3.2 En `PackageOrderService.handleUpdateInvoicedPackage`, envolver el bloque `downloadAndEncode → buildInvoiceDTO → sendInvoice → markInvoiceSent` en un único `try`. **No** se agrega ninguna llamada a `MiddlewareOrderService`/`updateAnymarketOrderTransmission` — removido del alcance (bloqueado por el SDK, ver proposal.md §7.3).
- [ ] 3.3 `catch (MarketplaceAccountException e)`: loguear con contexto (`OI`, `ORDER`, `packageIdItem`, mensaje de error), **no relanzar** — continuar el `for`. Sin reporte a AnyMarket.
- [ ] 3.4 No agregar ningún otro `catch` en este método — cualquier otra excepción (`MarketplaceUnavailableException`, la excepción de descarga de Task 2, etc.) debe propagarse sin capturar, para que `SendPackageConsumer` (extiende `AbstractRetryableConsumer`) la reintente con su política normal.
- [ ] 3.5 Test: `PackageOrderServiceTest` — error `MarketplaceAccountException` en un paquete no aborta el procesamiento de los siguientes; error de otro tipo sí se propaga fuera del método (verificar con `assertThrows` que la excepción sale de `handleUpdateInvoicedPackage`/`handleEvent`).

## Task 4 — [#2137396] Investigar múltiples invoices por paquete — ⚠️ NO IMPLEMENTADA (02/09)

> La implementación real (sesión de Code del 02/09) cubrió las Tasks 1/2/3/5/6 + Task A, pero **no tocó esta task**: nadie confirmó todavía (4.1) si el escenario de múltiples invoices por paquete es real, así que el código sigue con `packageItem.getInvoice().get(0)` sin el comentario de decisión de 4.2. Queda pendiente completa — no marcarla como hecha en el code review de mañana.

- [ ] 4.1 Confirmar con el equipo/documentación de Any'Core si un mismo `packageItem.getInvoice()` puede legítimamente traer más de un elemento en una sola notificación.
- [ ] 4.2 Si NO es un escenario real: dejar `packageItem.getInvoice().get(0)` como está, documentar la decisión con un comentario corto en el código (solo si el motivo no es obvio) junto al `get(0)`.
- [ ] 4.3 Si SÍ es real: cambiar `handleUpdateInvoicedPackage` para iterar todos los elementos de `packageItem.getInvoice()`, aplicando a cada uno el mismo flujo (Tasks 1-3 e idempotencia) — la clave de idempotencia (`invoiceNumberSent`) ya soporta esto sin cambios adicionales porque se valida por `invoiceNumber`, no por posición en la lista.
- [ ] 4.4 Sin test nuevo si el resultado es "no aplica" (4.2); si aplica (4.3), extender `PackageOrderServiceTest` con el caso de N invoices para un mismo paquete.

## Task nueva A — Idempotencia por paquete (no estaba en el desglose original — confirmado en scope con el usuario)

- [x] A.1 (implementado) Changelog Liquibase: `liquibase/resources/db/changelog/20260902_2137395_add_invoice_sent_columns_falabella_package.sql` (usa el ID de la Task 3/#2137395 porque la Task A nunca tuvo ID propio en ADO — confirmado con JP durante el techspec), agrega `INVOICE_NUMBER_SENT` (VARCHAR nullable) e `INVOICE_SENT_AT` (TIMESTAMP nullable) a `FALABELLA_PACKAGE`. **Corrección:** no hace falta registrar el archivo a mano en `db.changelog-master.yml` — usa `includeAll`, lo levanta automático. Aplicado limpio contra un schema descartable de Postgres local.
- [ ] A.2 Agregar los dos campos nuevos a la entidad `FalabellaPackage` (`@Column`, nullable).
- [ ] A.3 En `FalabellaPackageService`, agregar `boolean isInvoiceAlreadySent(String packageIdItem, String orderId, String invoiceNumber)` (compara contra `invoiceNumberSent` persistido).
- [ ] A.4 En `FalabellaPackageService`, agregar `void markInvoiceSent(String packageIdItem, String orderId, String invoiceNumber)` (setea `invoiceNumberSent` + `invoiceSentAt`, mismo patrón try/catch de `DataAccessException` que el resto de la clase).
- [ ] A.5 En `PackageOrderService.handleUpdateInvoicedPackage`: chequear `isInvoiceAlreadySent(...)` antes de intentar el envío (skip + log si ya fue enviado con ese mismo `invoiceNumber`); llamar `markInvoiceSent(...)` justo después de un `sendInvoice` exitoso.
- [ ] A.6 Test: `FalabellaPackageServiceTest` (casos existe-mismo-número / existe-número-distinto / no-existe) y `PackageOrderServiceTest` (skip por idempotencia, reintento legítimo con número de boleta distinto).

## Task 5 — [#2137398] Teste Cruzado

- [ ] 5.1 Validar de punta a punta los 9 escenarios de [test.md](./test.md) contra la implementación de las Tasks 1-4 + A.
- [ ] 5.2 Coordinar con el estado real de #1100204 en el ambiente de prueba (la notificación de paquete debe estar disponible de punta a punta).
- [ ] 5.3 Confirmar explícitamente que NO hay ninguna confirmación de status a AnyMarket en este flujo (comportamiento esperado, no un olvido) — evitar que QA lo reporte como bug.

## Task 6 — [#2137399] Code Review

- [ ] 6.1 Revisar que no haya duplicación/solapamiento con #1100204.
- [ ] 6.2 Revisar específicamente la distinción `MarketplaceAccountException` (no-retry) vs. cualquier otra excepción (retry) — es el punto más sutil de todo el cambio.
- [ ] 6.3 Revisar el changelog Liquibase nuevo (Task A.1) contra las reglas del proyecto (nunca modificar changelogs existentes, columnas nullable).
- [ ] 6.4 Confirmar cobertura de test de los 9 escenarios de `test.md`.
- [ ] 6.5 Confirmar que no se agregó ningún cambio a `OrderRemoteAPI`/`SendInvoiceOrderMessage`/`FalabellaInvoiceTranslator` — el endpoint `fiscalDocument` compartido queda fuera de alcance (ver proposal.md §7.1).

---

## Estado de implementación (02/09, sesión de Code)

Tasks 1, 2, 3, 5, 6 (ADO) + Task A (idempotencia) implementadas y verificadas con **61 tests en verde** (31 `PackageOrderServiceTest` + 10 `PackageTranslatorTest` + 16 `FalabellaPackageServiceTest` + 4 `InvoiceDocumentDownloadServiceTest`). Corrida completa del proyecto (1607 tests): 14 fallas preexistentes de otra feature ya "dirty" en la misma branch, no relacionadas a este PBI. **Task 4 (#2137396) NO implementada** — ver arriba. Sin commits ni push — todo como cambios locales en `feature/1095186_envio_boletas_multiples`. Code review previsto para la mañana del 03/09.

Decisiones tomadas durante la implementación, confirmadas con JP en su momento:
- Task 2 (#2137394): se creó una clase nueva `InvoiceDocumentDownloadException` en vez de reusar `MarketplaceContentProcessingException` como sugería design.md §2.4.
- `resolveOrderItemIds` (Task 1) quedó con visibilidad de paquete, no `private` estricto, para poder testearla sin reflection.

## Nota sobre la task nueva (A)

La task **A** (idempotencia) no estaba en el desglose original de pré-análise (#2137392, #2137394, #2137395, #2137396). Surgió del análisis conjunto en este documento y el usuario confirmó que está en alcance. Antes de empezar a implementarla, se recomienda:
- Crear la task correspondiente en ADO (o incorporarla explícitamente dentro de #2137395, dado que está directamente relacionada con el manejo de retry/errores que esa task ya cubre y que está "In Progress").
- Confirmar con quien esté trabajando activamente en #2137395 para no pisar el mismo archivo (`PackageOrderService.java`) en paralelo.

## Nota sobre lo que quedó explícitamente pendiente de decisión de equipo

- **Disparador `fiscalDocument` compartido (hipótesis de Pablo):** no cerrado por Nico en la reunión. Este documento asume que NO se construye en este PBI. Si el equipo decide más adelante que sí hace falta, es un PBI/alcance aparte que tocaría `OrderRemoteAPI`, `SendInvoiceOrderMessage`, `RemoteApiOrderInvoiceManual` y `FalabellaInvoiceTranslator` — ninguno de esos archivos aparece en las tasks de arriba.
- **Confirmación a AnyMarket (pedido de Nico):** confirmado como bloqueado por el SDK actual, no por decisión de producto. Si se necesita a futuro, requiere gestionar un cambio en `anymarket-marketplace-sdk-api` con el equipo dueño de esa librería — no es una task ejecutable dentro de este repositorio hoy.
- **Tabla local para Task 1 (reabierto 02/09):** contradicción sin resolver entre el hallazgo de código (no existe `OrderItemId` en la tabla local) y la confirmación verbal de JP/Pablo (sí existe y alcanza). Ver Task 1.0 arriba y proposal.md §7.2 — requiere 2 minutos con el repo abierto antes de implementar.
- **Asincronía del repackage hacia Falabella:** riesgo nuevo, sin task asignada. Ver proposal.md §7.4 — coordinar con Marcelo/Nico antes de cerrar el comportamiento de Task 1.3.
- **4 preguntas del deck de discovery sin responder:** la reunión se cortó a los ~41 min por otro compromiso del equipo, antes de llegar a ellas de forma explícita. Ver proposal.md §8 para el detalle — recomendable agendar una reunión de seguimiento corta antes de dar Task 3, Task 4 y el desglose completo por cerrados.
