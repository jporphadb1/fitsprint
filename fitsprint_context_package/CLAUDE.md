# FitSprint — Contexto para IA e desenvolvimento

FitSprint é uma ferramenta enxuta de apoio a Sprint Sizing: centraliza priorização de backlog (ratio valor/esforço), capacidade por developer e buffer de continuidade operativa para urgências. Projeto individual, mantido por 1 dev, com deploy contínuo na Railway.

Leia primeiro `docs/context/overview/project_goal_context.md` para o "porquê" do produto antes de mexer em qualquer regra de negócio.

## Stack

- Java 17 + Spring Boot 3.2, Maven (wrapper `mvnw`, build atual usa `-DskipTests` — não há suíte formal de testes ainda)
- Frontend: HTML + CSS + JS vanilla, servido estático por `src/main/resources/static/` (sem app frontend separada)
- Persistência: H2 em memória (dev), PostgreSQL gerenciado pela Railway (produção)
- Auth: Spring Security + JWT (7 dias), papéis `SUPER_ADMIN` / `ADMIN` / `USER`, multitenancy por `team_id`
- Sem Docker, sem orquestração, sem filas/mensageria — arquitetura é monólito em camadas de propósito (ver restrições abaixo)
- Swagger/OpenAPI em `/swagger-ui.html`, API sob `/api/v1/`

## Estrutura de contexto (`docs/context/`)

Esta pasta espelha a estrutura de documentação gerada pelo **Makuco** (ferramenta interna de discovery/spec assistida por IA, iniciativa AI-First). Antes de propor mudanças de arquitetura, escopo ou regra de negócio, consulte o documento relevante:

| Pasta | Conteúdo |
|---|---|
| `overview/` | Objetivo do projeto, problema, personas de alto nível, glossário de domínio |
| `product/` | Escopo detalhado por módulo/feature e o que está fora de escopo |
| `architecture/` | Padrão arquitetural, decisões técnicas, stack, restrições obrigatórias |
| `management/` | Como o desenvolvimento é conduzido (sessões individuais, Definition of Done) |
| `discovery/persona/` | Personas (Scrum Master, Developer, Super Admin) com objetivos, dores e permissões |
| `discovery/interviews/` | Registros de demos/entrevistas reais e insights que viraram features |
| `discovery/references/similar_systems/` | Análises comparativas (ex.: Azure DevOps Boards) e o que aprendemos delas |
| `FitSprint_Business_Rules_RAG.md` | Corpus de regras de negócio já implementadas — fonte de verdade de **comportamento real** do sistema (use para responder "como o sistema se comporta se X?") |

## Regras de negócio — fonte de verdade

Para qualquer dúvida de comportamento ("o que acontece se eu marcar uma bolina como `fuera`?", "como funciona o buffer?"), consulte `docs/context/FitSprint_Business_Rules_RAG.md` primeiro — ele descreve o sistema como ele realmente se comporta hoje, não a intenção original.

## Restrições importantes (não violar sem justificar)

- Nada de Kubernetes, microserviços, filas/brokers ou infra que exija operação dedicada
- Deploy deve continuar compatível com Railway
- Priorizar rapidez de implementação e baixo custo operacional sobre robustez enterprise
- Novas dependências: avaliar impacto em manutenção e curva de aprendizado antes de adicionar

## Como estes documentos foram gerados

Os arquivos em `docs/context/` (exceto o RAG de regras de negócio) foram produzidos via **Makuco**, como parte do desafio semanal do Comitê de IA (iniciativa AI-First). Ao evoluir o produto de forma relevante, re-exporte os documentos atualizados do Makuco e substitua os arquivos correspondentes aqui, mantendo os mesmos caminhos.
