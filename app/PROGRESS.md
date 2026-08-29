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
