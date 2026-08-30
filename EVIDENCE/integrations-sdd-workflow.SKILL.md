---
name: integrations-sdd-workflow
description: "Flujo de Spec-Driven Development para features y fixes en los repos Java/Spring Boot del equipo de Integrations: spec primero, implementación acotada, pruebas end-to-end y decisiones documentadas antes de cada commit."
---

# Integrations SDD Workflow

## Trigger

Se activa cuando el pedido es implementar una feature nueva, corregir un bug, o seguir Spec-Driven Development en cualquier repo del equipo. Es especialmente relevante cuando hay documentación de discovery/spec generada por Makuco (`docs/context/`), un RAG de reglas de negocio, o un work item de Azure DevOps que describe el comportamiento esperado.

## Pasos

1. **Leer la fuente de verdad antes de escribir código.** Ubicar y leer: la spec de discovery si existe (`docs/context/` de Makuco: escopo, arquitectura, restricciones técnicas), el documento de reglas de negocio ya operadas (si existe un RAG, es la fuente de verdad de *comportamiento real*, no la intención original), y el ticket/work item que originó el pedido. Si algo no está claro entre la intención original y el comportamiento real documentado, priorizar el comportamiento real.
2. **Nunca trabajar directo sobre `main`.** Crear una rama de feature/fix con nombre descriptivo antes de tocar cualquier archivo.
3. **Implementar en unidades pequeñas y verificables** — por módulo, por endpoint, por capa — no todo de una sola vez. Cada unidad debe poder probarse de forma aislada.
4. **Antes de cada commit: compilar, levantar la aplicación real (o el flujo relevante) y probar end-to-end contra ella.** No dar por buena una implementación solo porque compila. Incluir explícitamente casos borde (validaciones, roles/permisos, límites, escenarios de error) además del camino feliz.
5. **Documentar en un archivo de progreso** (ej. `PROGRESS.md` en la raíz del módulo/feature) por cada unidad de trabajo completada: qué se hizo, qué falta, y qué se decidió por criterio propio cuando la spec no era explícita — marcado claramente como "asumido" para revisión humana posterior, nunca oculto ni mezclado con lo que sí estaba especificado.
6. **Si el trabajo es la corrección de un bug de producción**, cerrar la documentación con el formato Problema / Solução / Como testar / Resultado esperado, describiendo la causa raíz real (no solo el síntoma reportado) y el escenario de reproducción concreto.
7. **Commitear con mensajes descriptivos** por cada unidad de trabajo cerrada. Nunca pushear a `main` ni hacer deploy sin que el humano lo pida explícitamente.

## Verification

- ¿Cada decisión que no estaba en la spec original quedó explícita en el archivo de progreso, en vez de asumida en silencio?
- ¿Se probó contra la aplicación corriendo (no solo compilación/build) antes de cada commit, incluyendo al menos un caso borde relevante por unidad?
- ¿La rama de trabajo nunca tocó `main` sin aprobación explícita del humano?
- ¿El build final compila limpio de punta a punta?
- Si era un bug: ¿la documentación final explica la causa raíz, no solo el parche aplicado?