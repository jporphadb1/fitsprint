# Definição de Arquitetura

## Padrão Arquitetural Adotado

**Padrão:** Monólito em Camadas (Layered Monolith)

**Justificativa:** O fistSprint é uma ferramenta interna com escopo funcional enxuto, construída inicialmente por um único desenvolvedor para apoiar uma cerimônia ágil específica e não um produto massivo. Nesse contexto, um monólito em camadas reduz o overhead de coordenação, simplifica o deploy e acelera a entrega, o que é coerente com o tamanho do time, o baixo nível de complexidade do domínio e a necessidade de colocar a solução em uso rapidamente. A escolha também reduz risco técnico ao concentrar backend, frontend e regras de negócio em um único deployable no Railway. Embora exista potencial futuro de evolução para SaaS multi-equipe, o estágio atual não justifica microserviços nem uma separação mais sofisticada por domínio. Ainda assim, decisões como centralizar o cálculo de priorização no backend já preparam o sistema para eventual expansão de interfaces sem duplicação de lógica.

---

## Como o Sistema está Organizado

O sistema é organizado como um único deployable Spring Boot que serve tanto a API REST quanto o frontend estático. O frontend é um único `index.html` em JavaScript vanilla servido diretamente de `src/main/resources/static/`, sem aplicação frontend separada. No backend, a organização segue separação por camadas técnicas clássicas: `controller -> service (interface) -> serviceImpl (lógica) -> repository -> entity`, com DTOs específicos de entrada e saída e uso de mapeadores com MapStruct entre entidades e DTOs. A API é exposta sob o prefixo `/api/v1/`. Não há divisão interna por módulos de domínio ou bounded contexts; os conceitos de negócio como Sprint, Bolina, Developer e Team compartilham a mesma estrutura por camada técnica, e a lógica de domínio — incluindo priorização, buffer e zonas — fica concentrada na camada `serviceImpl`.

---

## Decisões Arquiteturais Importantes

| Decisão | O que foi decidido | Justificativa |
|---|---|---|
| Estrutura da aplicação | O sistema será um monólito em camadas com organização técnica por controller, service, serviceImpl, repository e entity | Essa estrutura é a forma mais rápida e de menor risco para um produto pequeno, com um único desenvolvedor e deploy único |
| Entrega de frontend e backend | O Spring Boot serve o frontend estático e a API REST no mesmo deployable | Simplifica build, deploy e operação, evitando pipeline e infraestrutura separados para frontend |
| Organização da lógica de negócio | A lógica de domínio fica no backend, especialmente em `serviceImpl`, e não no frontend | Garante centralização das regras, evita duplicação e prepara o sistema para futuras interfaces além da web atual |
| Segurança e autenticação | Autenticação com Spring Security + JWT com expiração de 7 dias e papéis `SUPER_ADMIN`, `ADMIN` e `USER` | Atende a necessidade de controle de acesso por perfil mantendo implementação simples para um sistema interno |
| Isolamento multi-equipe | O sistema é multiusuário e multitenant por equipe, com filtragem por `team_id` extraído do JWT em todas as queries | Garante isolamento total entre equipes sem necessidade de separar a aplicação em instâncias diferentes |
| Persistência por ambiente | Uso de Spring Data JPA com H2 em memória em desenvolvimento e PostgreSQL em produção no Railway | Permite desenvolvimento rápido com setup leve e operação em produção com banco relacional persistente |
| Cálculo de priorização | O ratio de priorização (`valor / tamanho`) é calculado no backend | Evita depender do cliente para regra crítica e facilita reutilização da lógica por outras interfaces no futuro |
| Processamento | O sistema opera de forma 100% síncrona, sem filas, workers ou jobs assíncronos | O domínio atual não exige processamento desacoplado e a opção síncrona reduz complexidade operacional |
| Estratégia de remoção de dados | O sistema usa soft delete em vez de remoção física | Preserva histórico útil para retrospectivas e reduz perda de contexto operacional |

---

## Diagramas

**C1 — Contexto:** `architecture/diagrams/c4/c1-context.png` — visão do sistema no ecossistema
**C2 — Containers:** `architecture/diagrams/c4/c2-containers.png` — principais blocos e tecnologias
**C3 — Componentes:** `architecture/diagrams/c4/c3-components.png` — organização interna

---

> **Lembrete:** este documento descreve a intenção arquitetural. Quando houver divergência entre o que está aqui e o que está no código, o código deve ser corrigido — ou este documento deve ser atualizado com um ADR justificando a mudança.
