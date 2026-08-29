# FitSprint — Base de Conocimiento de Reglas de Negocio (para agente RAG)

Este documento es el corpus de referencia para un agente de IA con RAG que responde preguntas sobre las reglas de negocio de FitSprint (herramienta interna de apoyo a Sprint Sizing). Pensado para que Devs y POs consulten "¿cómo se comporta el sistema en tal caso?" sin tener que leer el código.

## Entidades y atributos

**Bolina** (tarea del sprint): nombre, tamaño (Fibonacci: 1,2,3,5,8,13,21), valor de negocio (número libre), tipo (URGENCIA, BUG_NUEVO, BUG_PLANEADO, REQ_PLANEADO), developer asignado (opcional), estado (TODO, IN_PROGRESS, DONE), fecha de creación, flag `fuera`, flag `eliminado` (soft delete).

## Regla: Ratio de prioridad

`ratio = valor / tamaño`, calculado en runtime (no persistido). A mayor ratio, más conveniente la tarea (mucho valor con poco esfuerzo). FitSprint ordena automáticamente las bolinas por este ratio, de mayor a menor.

## Regla: Zonas de prioridad

Las bolinas se dividen en dos zonas, mostradas siempre en este orden:
1. **Continuidad Operativa** (siempre primero): tipos URGENCIA y BUG_NUEVO, ordenadas entre sí por ratio descendente.
2. **Sprint Normal** (después): tipos BUG_PLANEADO y REQ_PLANEADO, ordenadas por ratio descendente.

## Regla: Buffer de Continuidad Operativa

El equipo define un % del sprint reservado para imprevistos (default 20%). Si la capacidad es 50 pts y el buffer es 20%, hay 10 pts reservados para urgencias/bugs nuevos.
Semáforo del indicador:
- 🟢 uso del buffer < 50%
- 🟡 uso del buffer entre 50% y 100%
- 🔴 uso del buffer > 100% — las urgencias se están comiendo trabajo planificado.

## Regla: flag "fuera"

Una bolina marcada `fuera` queda excluida del cálculo de capacidad del sprint, pero sigue siendo visible (con opacidad reducida). Al crear un nuevo sprint, todas las bolinas `fuera` del sprint activo se arrastran automáticamente al nuevo sprint.

## Regla: soft delete

Eliminar una bolina nunca la borra físicamente de la base de datos — se marca `eliminado = true`. Esto preserva el historial para retrospectivas. Una bolina eliminada no cuenta en ningún cálculo de capacidad ni aparece en la tabla priorizada activa.

## Regla: Sprint activo único

Solo puede existir un Sprint con `status = ACTIVE` por equipo a la vez. Al activar un sprint nuevo, el anterior pasa a `CLOSED` automáticamente.

## Regla: Multitenancy

Todos los queries filtran por `team_id`, extraído del JWT del usuario autenticado. Un usuario del Equipo A nunca puede ver ni modificar sprints, bolinas o developers del Equipo B — ni forzando IDs directamente en la URL/API.

## Regla: Roles

- `SUPER_ADMIN`: gestiona equipos y usuarios a nivel plataforma.
- `ADMIN`: gestiona el equipo (developers, sprints).
- `USER`: opera sprints y bolinas del día a día.

> Nota: este es el corpus de reglas de negocio ya operado por el sistema real (usa **% de la capacidad / story points** para el buffer). Si algún documento de `docs/context/overview/` describe el buffer en otra unidad, ese documento está desactualizado — este archivo es la fuente de verdad de comportamiento.
