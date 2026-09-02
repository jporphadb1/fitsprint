# Design — PBI 1095186: Envío de boletas múltiples (Falabella / Fluxo SS)

> Complementa [proposal.md](./proposal.md). Foco: **cómo** se cierran los 3 huecos identificados sobre el flujo ya existente en `PackageOrderService.handleUpdateInvoicedPackage`.

---

> **Nota importante:** el PBI ya tiene 6 tasks hijas creadas en ADO por una pré-análise previa (#2137392, #2137394, #2137395, #2137396, #2137398, #2137399 — ver [task.md](./task.md)). Este diseño se construye **sobre esas tasks**, no en paralelo: incorpora un bug adicional detectado ahí (Task 1 — `orderItemIds` mal armado) y precisa cómo implementar la Task 3 (no-retry de errores permanentes) usando la jerarquía de excepciones ya existente en el proyecto, sin inventar un parseo de código `E004`.

> **Reconciliación post-reunión de discovery (ver también proposal.md §7):** se investigó en código real la hipótesis de Pablo (endpoint `fiscalDocument` compartido con `packageId`) y la necesidad de confirmar a AnyMarket planteada por Nico. Resultado:
> 1. **Disparador: se mantiene el evento JMS `UPDATE_INVOICED_PACKAGE` (#1100204) como único disparador de este diseño.** El endpoint clásico `OrderRemoteAPI` (`PUT /order/{id}/invoice/document`) no tiene `packageId` hoy y su traductor (`FalabellaInvoiceTranslator`) envía siempre TODOS los `orderItemIds` del pedido — soportar `packageId` ahí requeriría lógica nueva no trivial, no solo agregar un campo. Queda documentado como riesgo abierto en proposal.md §7, no como parte de este diseño.
> 2. **Se corrige una premisa de la Task 1** (§2.1 más abajo): no existe ninguna tabla local con `packageId → OrderItemId real`. La resolución de `OrderItemId` sigue requiriendo una llamada en vivo a Falabella (`GetOrderItems`), correlacionada por SKU normalizado — no por comparar `packageId` contra el `PackageId` que devuelve Falabella (esa igualdad no está verificada en el código). **✅ Cerrado 02/09:** se había reabierto esta duda en un follow-up y se re-verificó durante la implementación real (`FalabellaPackageItem.java` líneas 27-40) — confirma que la entidad solo tiene `sku`+`quantity`, sin `orderItemId`. §2.1 se implementó tal cual está escrito abajo, sin cambios.
> 3. **Se remueve del alcance la confirmación a AnyMarket por paquete** (antes "Task nueva B"): se confirmó decompilando el SDK (`anymarket-marketplace-sdk-api`, versión en uso 1.47.100 y también la más nueva disponible localmente, 1.47.127) que `OrderStatusUpdate.withSuccess()` no admite ningún parámetro ni `packageId` — es una limitación real de la librería, no un detalle de implementación. Ver §2.8 y §4.

## 1. Flujo actual vs. flujo objetivo

**Actual** (`PackageOrderService.handleUpdateInvoicedPackage`, líneas 155-181):

```
for (packageItem : packages) {
    if (sin invoice) continue;
    falabellaPackageService.updatePackage(...);                 // persiste notificación cruda
    invoiceDTO = packageTranslator.buildInvoiceDTO(...);         // orderItemIds = [packageIdItem]      ← BUG (Task 1, #2137392)
                                                                  // invoiceDocument = invoice.getInvoiceUrl() ← BUG (Task 2, #2137394)
    documentService.sendInvoice(invoiceDTO, sellerConfiguration); // sin try/catch, sin confirmación a AnyMarket, sin distinguir error permanente/transitorio (Task 3, #2137395)
}
```

**Objetivo**:

```
for (packageItem : packages) {
    if (sin invoice) continue;

    orderItemIds = resolveOrderItemIds(packageItem, falabellaOrderItems);   // FIX Task 1 (#2137392)
    if (orderItemIds.isEmpty()) { log.error(...); continue; }               // AC de Task 1: log + no rompe el flujo

    invoice = packageItem.getInvoice().get(0);                              // Task 4 (#2137396) decide si esto cambia

    if (falabellaPackageService.isInvoiceAlreadySent(packageIdItem, orderId, invoice.getInvoiceNumber())) {
        log + continue;                                                    // idempotencia (nuevo, ver §2.6)
    }

    try {
        pdfBase64 = invoiceDocumentDownloadService.downloadAndEncode(invoice.getInvoiceUrl());  // FIX Task 2 (#2137394)
        invoiceDTO = packageTranslator.buildInvoiceDTO(orderItemIds, invoice, sellerConfiguration, pdfBase64);
        documentService.sendInvoice(invoiceDTO, sellerConfiguration);
        falabellaPackageService.markInvoiceSent(packageIdItem, orderId, invoice.getInvoiceNumber());
        // NO se confirma a AnyMarket (updateAnymarketOrderTransmission) — removido del alcance, ver §2.8.
    } catch (MarketplaceAccountException e) {
        // Error de NEGOCIO de Falabella devuelto en un 200 OK con ErrorResponse (boleta duplicada,
        // item en status inválido, etc. — lo que el PBI llama genéricamente "E004"). Es DEFINITIVO:
        // se traga aquí y se sigue con el próximo paquete. Task 3 (#2137395).
        log.error(...);
        // NO se relanza — el for continúa con el siguiente paquete.
        // NO se llama a middlewareOrderService acá tampoco (ver §2.8) — solo queda el log como
        // registro del fallo permanente; observabilidad vía Kibana, no vía Core.
    }
    // Cualquier otra RuntimeException (MarketplaceUnavailableException por 5xx/timeout de Falabella,
    // fallo de red al descargar el PDF, etc.) NO se captura acá: se deja propagar fuera de
    // handleUpdateInvoicedPackage para que el consumer JMS (AbstractRetryableConsumer) reintente el
    // mensaje SendPackageMessage completo con su política de retry/backoff normal (Task 3, #2137395).
    // La idempotencia de arriba es lo que evita reenviar de más los paquetes ya facturados con éxito
    // cuando ese reintento de mensaje completo efectivamente ocurre.
}
```

---

## 2. Archivos a modificar

### 2.1 `src/main/java/br/com/anymarket/marketplace/integration/marketplace/order/service/PackageOrderService.java`
- Método `handleUpdateInvoicedPackage(...)`: reescribir el cuerpo del `for` según el pseudocódigo objetivo.
- Nuevo método privado `resolveOrderItemIds(SendPackageItem packageItem, List<FalabellaOrderItem> falabellaOrderItems)` (Task 1, #2137392): filtra `falabellaOrderItems` (ya se obtienen vía `getFalabellaOrderItems(...)`, método ya existente en esta clase y usado hoy en `handleCreatedPackage`) por **SKU normalizado** de `packageItem.getItems()` (reusar `packageTranslator.normalizeSku(...)`, ya existe), y devuelve la lista de `OrderItemId` reales (`FalabellaOrderItem.getOrderItemId()` — confirmar nombre exacto del getter, ver `integration/marketplace/order/dto/item/FalabellaOrderItem.java`). Si no encuentra ningún ítem para ese paquete, loguea error y el llamador hace `continue` (no rompe el resto del for) — AC explícito de la Task 1.
  - **Corrección importante (post-discovery):** la correlación se hace **por SKU**, no comparando `packageIdItem` (identificador local del webhook de split) contra `FalabellaOrderItem.getPackageId()` (el `PackageId` que devuelve Falabella en `GetOrderItems`). No hay evidencia en el código de que ambos valores sean el mismo identificador — depender de esa igualdad sería una suposición no verificada. El SKU es el único dato que ambos lados comparten con certeza: el webhook de split (`SendPackageSkuItem.sku`) y la respuesta en vivo de Falabella (`FalabellaOrderItem.getSku()`).
  - **No existe ninguna tabla local reutilizable para esto.** Se investigó la hipótesis de que `FalabellaPackage`/`FalabellaPackageItem` (poblada en `handleCreatedPackage` a partir del webhook) pudiera evitar la llamada en vivo a Falabella — no es así: esa tabla solo persiste `sku` + `quantity` (lo único que trae el webhook, `SendPackageSkuItem`), nunca `OrderItemId`. Tampoco la respuesta de `orderService.repackage(...)` (la llamada que crea el split en Falabella) se captura hoy — `PackageOrderService.handleCreatedPackage` línea 111 descarta su `Response`. Conclusión: **resolver `OrderItemId` real siempre requiere la llamada en vivo `getFalabellaOrderItems` (`GetOrderItems`/`GetMultipleOrderItems`)**, tal como ya asumía este diseño — no hay atajo vía tabla local con los datos que existen hoy.
- Inyectar (constructor, `@RequiredArgsConstructor` ya presente): `InvoiceDocumentDownloadService invoiceDocumentDownloadService` (nueva clase, ver 2.4). **`MiddlewareOrderService` ya NO se inyecta** — ver §2.8.
- `falabellaPackageService` ya está inyectado — se le agregan las llamadas nuevas de idempotencia.
- Catch específico de `MarketplaceAccountException` (no `RuntimeException` genérico) alrededor del bloque `sendInvoice` — ver razonamiento en §4.

### 2.2 `src/main/java/br/com/anymarket/marketplace/integration/marketplace/order/translator/PackageTranslator.java`
- Método `buildInvoiceDTO(List<String> orderItemIds, SendPackageInvoice invoice, SellerConfiguration sellerConfiguration, String invoiceDocumentBase64)`:
  - Cambiar `.withOrderItemIds(List.of(packageItem.getPackageIdItem()))` → `.withOrderItemIds(orderItemIds)` (recibido ya resuelto desde `PackageOrderService`, Task 1, #2137392).
  - Cambiar `.withInvoiceDocument(invoice.getInvoiceUrl())` → `.withInvoiceDocument(invoiceDocumentBase64)` (Task 2, #2137394).
- La resolución de `orderItemIds` y la descarga/codificación del PDF **no** van acá (mantener el traductor libre de I/O de red y de lógica de filtrado) — se resuelven antes, en `PackageOrderService`, e inyectan el resultado ya calculado.

### 2.3 `src/main/java/br/com/anymarket/marketplace/integration/marketplace/document/dto/FalabellaInvoiceDTO.java`
- Sin cambios de forma — `invoiceDocument` ya es `String`. Solo cambia el contenido que se le pasa (Base64 en vez de URL).

### 2.4 (Nuevo) `src/main/java/br/com/anymarket/marketplace/integration/marketplace/document/service/InvoiceDocumentDownloadService.java`
- Servicio nuevo, responsabilidad única: descargar un PDF desde una URL (`invoice.getInvoiceUrl()`) y devolverlo codificado en Base64.
- Constructor: inyecta el bean `RestTemplate` ya existente (`ApplicationConfig.restTemplate`, `configuration/ApplicationConfig.java:68-83`) — **no crear un bean nuevo**, reusar el ya configurado (timeouts, `LoggingRequestInterceptor`, `TracingInterceptor`).
- Método público: `String downloadAndEncode(String invoiceUrl)`.
  - `byte[] bytes = restTemplate.getForObject(invoiceUrl, byte[].class);`
  - Validar `bytes` no nulo/vacío → si lo está, lanzar excepción de negocio (ver 2.6) para que el `catch` de `PackageOrderService` la capture y reporte como error de ese paquete.
  - `return Base64.getEncoder().encodeToString(bytes);`
  - Loguear con contexto `[INVOICE DOCUMENT] [OI: ...] [URL: ...]` en éxito/error, sin loguear el contenido base64 completo (solo tamaño en bytes).

### 2.5 `src/main/java/br/com/anymarket/marketplace/shared/domain/order/order_package/FalabellaPackage.java`
- Agregar dos columnas nuevas (nullable, no rompen filas existentes):
  - `@Column(name = "INVOICE_NUMBER_SENT") private String invoiceNumberSent;`
  - `@Column(name = "INVOICE_SENT_AT") private LocalDateTime invoiceSentAt;`
- Se eligió el nivel `FalabellaPackage` (no `FalabellaPackageItem`) porque la granularidad de "una boleta por paquete" coincide con la PK de esta entidad (`packageIdItem` + `orderId`).

### 2.6 `src/main/java/br/com/anymarket/marketplace/shared/domain/order/order_package/service/FalabellaPackageService.java`
- Nuevo método `boolean isInvoiceAlreadySent(String packageIdItem, String orderId, String invoiceNumber)`:
  - Busca por `falabellaPackageRepository.findByPackageIdItemAndOrderId(...)` (ya existe, usado en `updatePackage`).
  - Devuelve `true` si el registro existe y `invoiceNumberSent` es igual (no nulo) al `invoiceNumber` recibido — evita falso-idempotente si Falabella pide reenviar con un número de boleta distinto (reintento legítimo de negocio, no reintento técnico).
- Nuevo método `void markInvoiceSent(String packageIdItem, String orderId, String invoiceNumber)`:
  - Actualiza `invoiceNumberSent` + `invoiceSentAt = LocalDateTime.now(SYSTEM_ZONE)` y guarda.
  - Mismo patrón try/catch de `DataAccessException | IllegalStateException` + log que el resto de la clase (no debe hacer fallar el flujo si el `save` de la marca falla — como mucho, se reenviaría la boleta en el peor caso, que ya es tolerado por el error `E004` de Falabella, no un duplicado silencioso).

### 2.7 (Nuevo changelog Liquibase) `liquibase/resources/db/changelog/YYYYMMDD_2137394_add_invoice_sent_columns_falabella_package.sql`
> Sustituir `YYYYMMDD` por la fecha real de creación del changelog y `2137394` por el ID de task de ADO asociado (ver task.md). Seguir la convención del proyecto (`CLAUDE.md` — nunca modificar changelogs ya aplicados).

```sql
ALTER TABLE FALABELLA_PACKAGE ADD COLUMN INVOICE_NUMBER_SENT VARCHAR(255) NULL;
ALTER TABLE FALABELLA_PACKAGE ADD COLUMN INVOICE_SENT_AT TIMESTAMP NULL;
```
- Registrar el archivo en `liquibase/resources/db/changelog/db.changelog-master.yml`.

### 2.8 `MiddlewareOrderService.updateAnymarketOrderTransmission` — REMOVIDO del alcance (bloqueado por el SDK)
- **No se toca este archivo. No se inyecta `MiddlewareOrderService` en `PackageOrderService`.**
- Se había planteado (post-reunión de discovery) confirmar a AnyMarket el resultado del envío de cada boleta de paquete, acompañado del `packageId` correspondiente, para que la confirmación tuviera sentido a nivel de Core. Se investigó decompilando `anymarket-marketplace-sdk-api` (versión declarada en `pom.xml`, 1.47.100, y también la versión más nueva disponible en el repositorio Maven local, 1.47.127 — misma estructura en ambas):
  ```java
  public static OrderStatusUpdate.Builder withSuccess();              // sin parámetros
  public static OrderStatusUpdate.Builder withError(String);
  public static OrderStatusUpdate.Builder withError(OrderMessage);     // OrderMessage.parameters es Map<String,Object>
  ```
  `withSuccess()` no admite ningún parámetro — no hay forma de adjuntar `packageId` en una confirmación de éxito. Solo `withError(OrderMessage)` podría llevar un `packageId` dentro de `OrderMessage.parameters` (`Map<String,Object>`), pero no hay equivalente para el camino de éxito, y usar ese Map para un "éxito" sería un contrato no soportado por Core (el mismo `OrderMessage` está pensado semánticamente para mensajes de error).
- **Decisión (confirmada con el usuario):** se saca esta confirmación por completo del alcance de este PBI, tanto para éxito como para error. `PackageOrderService.handleUpdateInvoicedPackage` no llama a `MiddlewareOrderService` en ningún punto — el resultado de cada envío por paquete queda solo en logs (`[PACKAGE EVENT]`) y en la marca de idempotencia local (`FalabellaPackage.invoiceNumberSent`), no en Core.
- Si en el futuro se necesita este feedback a Core, requiere gestionar con el equipo dueño de `anymarket-marketplace-sdk-api` un campo `packageId` en `OrderStatusUpdate` (o un mecanismo equivalente) — está fuera del control de este repositorio.

---

## 3. Archivos de test a crear/actualizar

| Archivo | Cambio |
|---|---|
| `src/test/java/br/com/anymarket/marketplace/integration/marketplace/order/service/PackageOrderServiceTest.java` | Ampliar sección `handleUpdateInvoicedPackage`: resolución de `orderItemIds` por SKU, error permanente (`MarketplaceAccountException`) en un paquete que no aborta los siguientes ni se relanza, error transitorio que sí se propaga, e idempotencia (paquete ya facturado se salta). Mockear `InvoiceDocumentDownloadService`. **No** se mockea/verifica `MiddlewareOrderService` — ya no se usa (ver §2.8). |
| `src/test/java/br/com/anymarket/marketplace/integration/marketplace/order/translator/PackageTranslatorTest.java` | Actualizar `buildInvoiceDTO` para el nuevo parámetro `invoiceDocumentBase64`; verificar que `invoiceDocument` del DTO resultante sea el Base64 recibido, no la URL. |
| `src/test/java/br/com/anymarket/marketplace/integration/marketplace/document/service/InvoiceDocumentDownloadServiceTest.java` (nuevo) | Descarga exitosa → Base64 correcto; `RestTemplate` devuelve null/vacío → excepción; `RestTemplate` lanza excepción de red → se propaga como excepción de negocio. |
| `src/test/java/br/com/anymarket/marketplace/shared/domain/order/order_package/service/FalabellaPackageServiceTest.java` | Nuevos casos para `isInvoiceAlreadySent` (existe con mismo número → true; existe con número distinto → false; no existe → false) y `markInvoiceSent` (persiste columnas nuevas; error de BD no relanza excepción). |

---

## 4. Decisiones de diseño y alternativas descartadas

- **¿Cómo se distingue un error permanente (E004: boleta duplicada / item en status inválido) de uno transitorio (5xx/timeout), si no hay ningún código `"E004"` hardcodeado en el repo?** Por **tipo de excepción**, no por parseo de texto: `RequestExecutor.executeWithResilience` ya mapea 500/502/503/504 a `MarketplaceUnavailableException` (error transitorio de infraestructura). `DocumentService.sendInvoice` (línea 85-87), en cambio, lanza `MarketplaceAccountException` cuando Falabella responde **200 OK con un `ErrorResponse` en el body** — que es exactamente el caso de los errores de negocio tipo "E004" (duplicado, status inválido): la request llegó bien, pero Falabella la rechaza a nivel de reglas. `PackageOrderService` solo necesita capturar `MarketplaceAccountException` específicamente (no `RuntimeException` genérico) para tragar el error permanente y seguir con el siguiente paquete; todo lo demás (`MarketplaceUnavailableException`, fallos de red al descargar el PDF, etc.) se deja propagar para el retry normal del consumer JMS. Esto resuelve la Task 3 (#2137395) sin tener que parsear ningún código de error nuevo.
- **¿Por qué agregar idempotencia por columna (`FalabellaPackage.invoiceNumberSent`) si ya se distingue el error permanente?** Porque el retry transitorio (Task 3) sigue existiendo: si el paquete 2 de 3 falla con `MarketplaceUnavailableException`, la excepción se propaga, el consumer JMS reintenta el **mensaje completo**, y sin esta marca se reenviaría de nuevo la boleta del paquete 1 (que ya había tenido éxito) — generando ahí sí un E004 de duplicado evitable. La columna nueva es lo que hace ese reintento de mensaje completo seguro/idempotente.
- **¿Por qué no reintentar automáticamente la descarga del PDF dentro de esta misma ejecución?** Se prefiere dejar que la excepción de descarga se clasifique igual que cualquier otro error transitorio (propague → retry de mensaje completo con el backoff ya existente de `FalabellaRetryPolicy`/`FalabellaBackOffPolicy`), en vez de armar un mecanismo de reintento ad-hoc solo para el PDF.
- **¿Por qué no tocar `DocumentService.sendInvoice`?** El pipeline HTTP (HMAC, retry, circuit breaker) ya es agnóstico de "pedido completo vs. paquete" — el problema está en la capa de orquestación/traducción, no en la capa de transporte. Tampoco hace falta que empiece a distinguir tipos de error de negocio: ya lanza `MarketplaceAccountException`, que es justo el tipo que `PackageOrderService` necesita capturar.

---

## 5. Disparador: por qué NO se toca el endpoint `fiscalDocument`/`invoice/document` en este diseño

Investigación de código (post-reunión de discovery, hipótesis de Pablo):

- `OrderRemoteAPI.sendInvoice` (`PUT /order/{idInMarketplace}/invoice/document`) y `sendManualInvoice` (`.../manual`) son endpoints **entrantes** (AnyMarket Core llama hacia este conector — confirmado por el javadoc de `OrderRemoteAPIWireMockTest` y porque no existe ningún cliente saliente hacia esa ruta en el repo). Hoy **no tienen ningún parámetro `packageId`**, en ninguno de los dos (`SendInvoiceOrderMessage` ni `RemoteApiOrderInvoiceManual`).
- Agregar el campo `packageId` a esos DTOs sería trivial (son POJOs Jackson simples, sin validación estricta, campo opcional no rompe nada). **Lo que no es trivial es usarlo**: `FalabellaInvoiceTranslator.generateInvoiceDTO` arma hoy `orderItemIds` con **todos** los ítems del pedido (`middlewareOrderService.getFalabellaOrderItems(anymarketOrder)`, sin ningún filtro) — habría que construir lógica de filtrado por paquete ahí también, que hoy no existe en absoluto.
- **Decisión (confirmada con el usuario):** este diseño mantiene el evento JMS `UPDATE_INVOICED_PACKAGE` (#1100204) como **único disparador**. No se modifica `OrderRemoteAPI`, `SendInvoiceOrderMessage`, `RemoteApiOrderInvoiceManual`, `BusinessOrderInvoiceService` ni `FalabellaInvoiceTranslator` en este PBI.
- **Riesgo abierto, no resuelto por el equipo en la reunión:** si Core efectivamente termina invocando el endpoint `fiscalDocument` con `packageId` en el futuro (la hipótesis de Pablo no fue descartada, solo no está construida hoy), habría **dos caminos que podrían intentar facturar el mismo paquete**. La idempotencia diseñada en §2.6 (`FalabellaPackageService.isInvoiceAlreadySent`/`markInvoiceSent`, clave `packageIdItem` + `orderId` + `invoiceNumber`) es agnóstica de qué disparador la invoque, así que **ya cubriría ese escenario sin cambios adicionales** si/cuando se construya el segundo camino — pero eso está fuera del alcance de este PBI. Ver proposal.md §7 para el detalle completo de esta reconciliación.

---

## 6. Zonas sensibles tocadas (ver CLAUDE.md — "Zonas que NUNCA tocar sin análisis previo")

- Se agrega un changelog Liquibase **nuevo** (no se modifica ninguno existente).
- No se toca `FalabellaProductBodyCleaner`, `AnymarketStatusTransitionService`, `StatusTransitionService.generatePipeline()`, `JmsConfig` ni `bootstrap.yml`.
- `FalabellaPackage`/`SentProduct` no están en la lista de entidades `EAGER` documentadas como deuda técnica — confirmar el `FetchType` real de la relación `items` (`FalabellaPackage.items`, hoy `LAZY`) no cambia con este PBI.
