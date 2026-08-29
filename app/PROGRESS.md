# Progreso de implementación — FitSprint (app/)

Registro incremental: qué se hizo, qué falta, qué se asumió. Un bloque por commit.

---

## Skeleton transversal (base, no es un módulo del roadmap)

**Hecho:**
- Proyecto Maven completo (`pom.xml`, `mvnw`/`mvnw.cmd`, wrapper 3.2.0) — Spring Boot 3.2.12, Java 17.
- Dependencias: web, data-jpa, security, validation, H2 (dev), PostgreSQL (prod), JJWT 0.12.5, MapStruct 1.5.5, Lombok, springdoc-openapi.
- Entidades base con Lombok: `Team`, `Developer`, `User` (roles `SUPER_ADMIN`/`ADMIN`/`USER`), `Sprint`, `Bolina`.
- Enums de dominio: `Role`, `TaskType` (con mapeo obligatorio a `ZonaOperativa`), `ZonaOperativa`, `TaskStatus` (solo TODO/IN_PROGRESS/DONE), `SprintStatus`, `Importancia`.
- Repositorios base (`TeamRepository`, `UserRepository`, `DeveloperRepository` con suma de capacidad por team, `SprintRepository`, `BolinaRepository` vacío — se completa por módulo).
- Auth JWT completo: `JwtTokenProvider` (HS384, 7 días), `JwtAuthenticationFilter`, `SecurityConfig` (stateless, rutas públicas `/api/v1/auth/**`, `/h2-console/**`, swagger), `AuthController` + `POST /api/v1/auth/login`.
- `CurrentUser` (utilidad estática para extraer `AuthenticatedUser`/`teamId` del `SecurityContext` — usado por todos los módulos para filtrar por multitenancy).
- `GlobalExceptionHandler` con `ResourceNotFoundException`, `BusinessRuleException`, validación, credenciales inválidas, 500 genérico.
- `DataSeeder` (solo perfil dev, `fitsprint.seed.enabled=true`): 1 team, 3 usuarios (`super@fitsprint.local`, `admin@fitsprint.local`, `dev@fitsprint.local`, password `changeit`), 3 developers, 1 sprint ACTIVE.
- `application.yml` con perfiles `dev` (H2 en memoria) y `prod` (Postgres vía env vars `SPRING_DATASOURCE_*`, `JWT_SECRET`, `PORT` — compatible Railway, sin Docker).
- Verificado manualmente: `mvn compile` OK, boot completo OK, seeder corrió, login devuelve JWT válido, endpoint protegido sin token responde 403, login con password incorrecta responde 401.

**Falta:**
- Los 5 módulos de negocio (ver abajo).
- Frontend estático real (`static/index.html` es solo un placeholder).
- Endpoints de gestión de usuarios/developers/teams (CRUD) — no están especificados como módulo en `scope_features_context.md`, así que no se construyeron más allá de lo necesario para login. Si se necesita alta de usuarios más allá del seed, hay que definir ese flujo.
- Tests automatizados (el propio `tech_stack_context.md` dice que el proyecto real tampoco los tiene todavía).

**Asumido (decisiones sin supervisión, marcadas para revisión):**
- `Developer.capacidadeTotal` es un atributo del developer, no versionado por sprint (no hay modelado de calendarios de capacidad, explícitamente fuera de alcance).
- `User` (cuenta de acceso) y `Developer` (miembro del team para asignación de bolinas) son entidades separadas, sin vínculo entre sí en el MVP. Un usuario `USER`/`ADMIN` no está necesariamente ligado a un `Developer` — el spec no exige esa relación.
- `Bolina.teamId` está denormalizado (copiado de `sprint.team.id`) para poder filtrar por `team_id` directamente en cada query, tal como exige `architecture_definition_context.md`, sin depender de un join a `sprint`.
- No existe endpoint de registro de usuarios; el bootstrap de cuentas se resuelve con el `DataSeeder` en dev. En producción habría que decidir cómo se crean usuarios (fuera de alcance de este scaffold).
- `TaskStatus` sigue el enum de 3 valores real (`TODO`/`IN_PROGRESS`/`DONE`) de `scope_features_context.md` y `FitSprint_Business_Rules_RAG.md`, ignorando el estado `Bloqueada` de 4 valores que aparece en `glossary_context.md` — el propio RAG marca ese glosario como potencialmente desactualizado.
- No se generó Dockerfile ni configuración de contenedores (prohibido por `tech_restrictions_context.md`); el deploy apunta al build automático de Railway.

---

## Módulo 1: Backlog priorizado

**Hecho:**
- `Bolina` CRUD completo sobre el sprint activo del team del usuario autenticado: crear (`POST /api/v1/bolinas`), listar priorizado (`GET /api/v1/bolinas`), buscar por id, editar contenido (`PUT`), soft delete (`DELETE`).
- Endpoints dedicados de clasificación: `PATCH .../importancia` (manual, ALTA/MEDIA/BAIXA), `PATCH .../prioridade-final` (override manual del orden, nullable para volver a depender del ratio), `PATCH .../estado` (TODO/IN_PROGRESS/DONE), `PATCH .../fuera`.
- Ratio (`valor / tamanho`) y zona operativa calculados en runtime vía getters `@Transient` en la entidad, nunca persistidos.
- Validación de tamaño Fibonacci (1,2,3,5,8,13,21) en creación y edición — rechaza con 422 (`BusinessRuleException`).
- Orden del backlog: prioridad final manual primero (cuando existe), si no ratio descendente, empate por fecha de creación — implementado con un `Comparator` en memoria (no en SQL, para evitar diferencias de `NULLS FIRST/LAST` entre H2 y Postgres).
- `ActiveSprintResolver` (reutilizable por los próximos módulos) resuelve el sprint `ACTIVE` del team del JWT.
- Multitenancy: todo acceso pasa por `findByIdAndTeamId`, nunca por `findById` puro — un id de otro team responde 404, no 403 (evita confirmar existencia cross-team).
- Probado manualmente end-to-end: creación de 3 bolinas con distinto ratio, verificación del orden esperado, override manual de prioridad reordenando por encima del ratio, clasificación de importancia, tamaño inválido → 422, soft delete → desaparece de la lista.

**Falta:**
- Asignación de responsable (`developerId`) — deliberadamente NO incluida acá; es responsabilidad del módulo Ocupação e avanço (mapa de alocação, atribuir/reatribuir), que se construye después.
- No hay endpoint para crear/cerrar Sprints (el seeder ya deja uno ACTIVE); no estaba pedido como módulo y lo dejo fuera de alcance.

**Asumido:**
- El spec dice "PO ou Scrum Master" definen la importância manual, pero el sistema solo modela roles técnicos `SUPER_ADMIN`/`ADMIN`/`USER` (no hay rol PO/SM en el modelo de auth). No restringí estos endpoints por rol más allá de "autenticado + mismo team" — no hay mapeo claro persona-de-negocio → `Role` técnico en los docs. Si se define ese mapeo, hay que agregar `@PreAuthorize` en `BolinaController`.
- `prioridadeFinal` se asume ascendente (1 = primera en ejecutarse) por convención, ya que el spec no explicita la dirección del número.

---

## Módulo 2: Buffer e urgências

**Hecho:**
- `GET /api/v1/buffer`: capacidad total del sprint (suma de `capacidadeTotal` de developers del team), buffer reservado (`ceiling(capacidad × %buffer)`), buffer consumido (suma de `tamanho` de bolinas `URGENCIA`/`BUG_NUEVO` del sprint activo, excluyendo `fuera` y `eliminado`, sin importar estado ni responsable), buffer disponible, % de uso y semáforo.
- Casos especiales del semáforo implementados exactamente como en `FitSprint_Business_Rules_RAG.md`: reservado=0 y consumido=0 → 0% y VERDE; reservado=0 y consumido>0 → sin cálculo de porcentaje (null), directo VERMELHO; en el resto, VERDE <50%, AMARELO 50–100% inclusive, VERMELHO >100%.
- La clasificación de zona (`CONTINUIDAD_OPERATIVA`/`SPRINT_NORMAL`) ya estaba resuelta desde el módulo base (`TaskType.getZona()`); este módulo solo la consume, no la reimplementa en SQL — evita duplicar la regla de negocio en dos lugares.
- Probado end-to-end: buffer vacío (0%, VERDE), con una urgencia (AMARELO), sumando otra hasta superar el 100% (VERMELHO), y verificado que marcar una tarea como `fuera` la saca del cálculo de consumido inmediatamente (sin proceso de consolidación aparte, tal como exige la regla de negocio).

**Falta:**
- Nada pendiente de las 3 features del módulo (zonas, cálculo de buffer, semáforo). La "vista de urgências" (listado filtrado por zona) es explícitamente del módulo 5 (Filtros e vistas), no de este.

**Asumido:**
- `percentualUso` se redondea a 2 decimales para lectura humana; el semáforo se calcula sobre el valor exacto antes de redondear (evita que un redondeo mueva el semáforo de rango, aunque en la práctica la diferencia es despreciable).
- `bufferDisponivel` puede ser negativo cuando el consumo supera la reserva — se deja así deliberadamente porque es información operativa útil (cuánto se pasó del buffer), y el semáforo ya cubre la señal binaria de alerta.

---

## Módulo 3: Capacidade do time

**Hecho:**
- `GET /api/v1/capacidade`: una sola vista que trae capacidad total/usada/disponible por developer Y el consolidado del team en la misma respuesta (no dos endpoints separados), tal como pide el spec ("preservar a relação entre visão macro do time e detalhe por developer, sem esconder os desequilíbrios internos").
- Extraje `OcupacaoCalculator` (nuevo, en `service/`) como pieza compartida: agrupa story points por `developer_id` sobre las bolinas del sprint activo, excluyendo `fuera`/`eliminado`, sin filtrar por estado — esto es literalmente la regla de "ocupação" del módulo 4, pero la necesito ya acá porque "capacidade usada" es la misma cifra. La reutilizo (no la reimplemento) cuando construya el módulo Ocupação e avanço a continuación.
- Developers sin tareas aparecen con `capacidadeUsada=0` (no quedan fuera del listado ni rompen el cálculo).
- Probado end-to-end el caso base (sin asignaciones): total 42, usada 0, disponible 42, y cada developer mostrando su propia capacidad total intacta.

**Falta:**
- Validación con datos reales de `capacidadeUsada > 0` — todavía no hay forma de asignar un developer a una bolina (ese endpoint es del módulo 4, Ocupação e avanço, que sigue). Voy a re-probar este mismo endpoint apenas exista la asignación, antes de dar el módulo 4 por cerrado.

**Asumido:**
- Igual que en Buffer, `capacidadeDisponivel` puede ser negativo (developer sobrecargado) — mostrarlo así es información, no un error; la señalización explícita de sobrecarga (booleano/flag) es responsabilidad del módulo Ocupação e avanço, no de este.

---

## Módulo 4: Ocupação e avanço

**Hecho:**
- `PATCH /api/v1/bolinas/{id}/responsavel` (vive en `BolinaController`/`BolinaService`, no en un controller separado, porque muta directamente el recurso Bolina): asigna, reasigna o desasigna (`developerId: null`) el responsable. Valida que el developer pertenezca al mismo team que la bolina (`BusinessRuleException` si no); no hay restricción por rol técnico — ADMIN y USER pueden ambos, sin exclusividad administrativa, tal como pide el spec.
- `GET /api/v1/ocupacao/mapa`: todas las bolinas no eliminadas del sprint activo (incluye `fuera` — solo se ocultan las eliminadas, `fuera` sigue visible con su flag ya presente en `BolinaResponse`), con responsable o `developerNome: null` ("Sin asignar").
- `GET /api/v1/ocupacao/disponibilidade`: por developer, capacidad total, ocupación actual, disponible, cantidad de tareas y `status` (`SEM_TAREFAS` / `DENTRO_DA_CAPACIDADE` / `SOBRECARREGADO`). Sobrecarga se compara contra 0 cuando el developer no tiene capacidad configurada (ya cubierto porque `capacidadeTotal` nunca es null en el cálculo).
- Refactor de `OcupacaoCalculator`: ahora devuelve `Map<Long, Ocupacao(storyPoints, quantidadeTarefas)>` en vez de solo la suma, porque el módulo necesitaba también el conteo de tareas para decidir `SEM_TAREFAS`. Actualicé `CapacidadeServiceImpl` (módulo 3) para usar el nuevo tipo — mismo cálculo, un solo lugar.
- Reprobé el módulo 3 (`GET /api/v1/capacidade`) con datos reales de asignación, como quedó pendiente en su bloque: confirma `capacidadeUsada`/`capacidadeDisponivel` correctos con Carla sobrecargada (10/8, disponible -2).
- Probado end-to-end: asignar 2 tareas al mismo developer hasta sobrecargarlo (`SOBRECARREGADO`), reasignar una a otro developer (ambos vuelven a `DENTRO_DA_CAPACIDADE`), desasignar (`SEM_TAREFAS`), developer inexistente → 404, mapa de alocación reflejando responsables y "Sin asignar" correctamente.

**Falta:**
- Nada pendiente de las 4 features del módulo. "Vista operacional limpa" (ocultar eliminadas, sin histórico intermedio) ya queda cubierta porque el mapa siempre relee en runtime desde `findAllBySprintIdAndEliminadoFalse` — no hay estado persistido de ocupación que pueda desincronizarse.

**Asumido:**
- No validé "developer pertenece al mismo team que la bolina" contra un segundo team real (no armé un segundo team en el seeder) — la lógica se revisó por código pero solo se probó el camino feliz (mismo team) y el camino de developer inexistente. Si se agrega un segundo team al seeder más adelante, vale la pena reprobar ese caso específico.

---

## Módulo 5: Filtros e vistas (último módulo del roadmap — scaffold completo)

**Hecho:**
- `GET /api/v1/vistas/principal`: vista priorizada del sprint activo con filtros combinados por AND — `developerId`, `semResponsavel` (bolinas sin responsable, "Sin asignar"), `estado` (TODO/IN_PROGRESS/DONE), `zona` (CONTINUIDAD_OPERATIVA/SPRINT_NORMAL). Todos opcionales vía query params; combinaciones sin resultado devuelven `[]` con 200, nunca error.
- `GET /api/v1/vistas/urgencias`: solo zona CONTINUIDAD_OPERATIVA del sprint activo.
- `GET /api/v1/vistas/disponibilidade`: delega 100% a `OcupacaoService.disponibilidadePorDeveloper()` (módulo 4) — no reimplementa el cálculo, tal como corresponde a un módulo "estritamente de consulta".
- Extraje la regla de ordenación del backlog a `BacklogOrdenacao` (antes vivía privada en `BolinaServiceImpl`) para que este módulo la reutilice sin duplicar la lógica de prioridad manual/ratio/desempate.
- Ningún endpoint de este controller escribe nada — solo `@GetMapping`, consistente con "módulo estritamente de consulta" del spec (sin edición de estado/prioridad/responsável, sin guardado de vistas, sin exportación, sin kanban).
- Probado end-to-end con 4 bolinas variadas: filtro simple por estado, por zona, por developer, por "sin asignar", combinación AND de dos filtros, combinación AND sin resultados (lista vacía, sin error), vista de urgencias, y vista de disponibilidad devolviendo la misma forma que el módulo 4.

**Falta:**
- Nada pendiente de las 5 features nombradas del módulo (incluyendo la negativa explícita: no se implementó edición, guardado de vistas, exportación, kanban ni filtro por fecha — todo eso está fuera de alcance según `scope_features_context.md`).

**Asumido:**
- Ninguna asunción nueva relevante en este módulo; reutiliza las decisiones ya registradas en Backlog (orden), Buffer/Capacidade (cálculos) y Ocupação (disponibilidade).

---

## Cierre del scaffold inicial

Los 6 bloques pedidos (skeleton transversal + 5 módulos de `scope_features_context.md`) están implementados, compilados y probados manualmente end-to-end sobre H2 en cada commit. Pendiente real para un siguiente paso: frontend estático (hoy es un placeholder), tests automatizados, y las decisiones marcadas como "Asumido" en cada bloque — en particular el mapeo entre roles técnicos (`SUPER_ADMIN`/`ADMIN`/`USER`) y las personas de negocio (PO/Scrum Master/Developer) si se quiere restringir endpoints por rol más adelante.

---

## Autorización — @PreAuthorize según discovery/persona/Sofia_context.md

El mapeo pedido de roles: Sofía (Scrum Master/Tech Lead) = `ADMIN`, Martín (Developer) = `USER`, Valentina (Super Admin) = `SUPER_ADMIN`. La tabla de permisos de Valentina confirma explícitamente *"É o único papel não limitado por team_id"* pero que sí opera "Operação funcional de sprint por equipe... quando necessário" — es decir, SUPER_ADMIN no tiene team propio, pero puede actuar sobre un team_id explícito cuando lo necesita.

**Decisión de diseño derivada de ese matiz — `CurrentUser.resolveTeamId(Long requestedTeamId)` (nuevo en `security/CurrentUser.java`):**
- SUPER_ADMIN: debe informar `teamId` explícito en el request (query param o campo del body); si no lo informa, `BusinessRuleException` (422). No se le permite "adivinar" un team propio porque no tiene.
- ADMIN/USER: usan siempre su propio `teamId` del JWT; si informan un `teamId` distinto al propio, `BusinessRuleException` (evita que un ADMIN opere sobre otro team aunque lo pida explícitamente).
- Para operaciones sobre un recurso puntual por id (buscar/editar/eliminar un Developer o Sprint específico), SUPER_ADMIN se resuelve con `findById` liso (bypassea el filtro de team, consistente con "no limitado por team_id"); ADMIN/USER siguen usando `findByIdAndTeamId`.

**Hecho — CRUD nuevos, no pedidos como módulo pero necesarios para poder asegurar la matriz de permisos:**
- `Team` CRUD global (`POST/GET/GET{id}/PUT/DELETE /api/v1/teams`) — `@PreAuthorize("hasRole('SUPER_ADMIN')")` a nivel de controller.
- `User` CRUD global (`POST/GET/GET{id}/PUT/DELETE /api/v1/users`) — mismo `@PreAuthorize`. Valida email único, hashea password con `PasswordEncoder`, exige `teamId` para ADMIN/USER y lo fuerza a `null` para SUPER_ADMIN.
- `Developer` CRUD acotado a equipo (`POST/GET/GET{id}/PUT/DELETE /api/v1/developers`) — `@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")`. `GET` y `POST` aceptan `teamId` opcional (obligatorio solo para SUPER_ADMIN) vía `resolveTeamId`.
- `Sprint` create/activate/close (`POST /api/v1/sprints`, `PATCH .../ativar`, `PATCH .../fechar`) — mismo `@PreAuthorize`. **Decisión de diseño:** `criar` deja el sprint en `CLOSED` (no lo activa); solo `ativar` aplica la regla "único ACTIVE por team" (cierra automáticamente el que estuviera activo). Separar create de activate fue un pedido explícito del usuario, y el enum `SprintStatus` no tiene un tercer estado tipo `DRAFT`, así que "recién creado, aún no activo" se modela como `CLOSED`.
- `@EnableMethodSecurity` agregado a `SecurityConfig` para habilitar `@PreAuthorize`.

**Probado end-to-end:** Team/User creación como SUPER_ADMIN (201) y como ADMIN/USER (403 en ambos); Developer creación como ADMIN sin teamId (usa el propio, 201), como USER (403), como SUPER_ADMIN sin teamId (422) y con teamId (201); Sprint creación como ADMIN (201, CLOSED) y como USER (403); activar Sprint 2 cierra automáticamente Sprint 1 y el módulo Capacidade pasa a leer sobre el sprint recién activado (confirmado por HTTP).

**Falta / asumido:**
- `Team.remover` y `User.remover` son hard delete (sin soft-delete ni cascada). Si existen Developers/Users/Sprints/Bolinas referenciando el team, el delete va a fallar por FK constraint — no implementé cascada porque no fue pedido; queda como limitación conocida.
- No armé un segundo team real en el seeder para probar el camino "ADMIN intenta operar sobre team ajeno → rechazado"; la lógica de `resolveTeamId` se revisó por código pero ese caso específico no se ejecutó contra la app corriendo.
- Sigue pendiente aplicar `@PreAuthorize` + `resolveTeamId` a los endpoints existentes de Bolina (importância/prioridade-final → ADMIN+SUPER_ADMIN; resto → USER+ADMIN+SUPER_ADMIN) y a las vistas de lectura (Buffer/Capacidade/Ocupação/Filtros → USER+ADMIN+SUPER_ADMIN) — se hace en el próximo bloque de este mismo pedido.
