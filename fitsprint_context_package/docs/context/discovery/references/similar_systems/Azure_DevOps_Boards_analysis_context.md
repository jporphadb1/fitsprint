# Análise de Sistema Similar

## Identificação

**Sistema analisado:** Azure DevOps Boards
**URL / Acesso:** https://dev.azure.com/ (requer organização/projeto no Azure DevOps)
**Empresa / Origem:** Microsoft
**Data da análise:** 2026-08-24
**Analisado por:** Autor do FitSprint

---

## Visão Geral

Azure DevOps Boards é o módulo de gestão ágil do ecossistema Azure DevOps, voltado para organização de backlog, planejamento de sprint e acompanhamento de execução do trabalho.

No contexto analisado, ele oferece hierarquia de work items (Epic → Feature → User Story/PBI → Task), backlog por iteração/sprint, taskboard em tempo real e uma ferramenta de capacidade para estimar quanto cada membro do time consegue entregar no sprint.

É uma solução ampla de tracking e gestão de trabalho, útil para times que precisam conectar planejamento, execução e engenharia em um mesmo fluxo.

---

## Funcionalidades Relevantes

- Ordenação manual do backlog (stack rank), por arraste ou edição de prioridade numérica.
- Planejamento de capacidade por pessoa, considerando horas por dia e dias de ausência.
- Comparação entre capacidade total do sprint e horas de trabalho atribuídas.
- Taskboard com acompanhamento visual do sprint por estado (To Do / Doing / Done).
- Hierarquia estruturada de trabalho entre Epic, Feature, PBI/User Story e Task.

---

## Pontos Positivos

| Ponto positivo | Por que é bom | Podemos aplicar? |
|---|---|---|
| Capacidade planejada por pessoa, com horas por dia e dias livres | Dá uma leitura mais granular da disponibilidade real do time e reduz planejamento baseado em média genérica | Adaptar |
| Integração nativa com backlog, código, PRs e pipelines | Conecta planejamento e execução no mesmo ecossistema, reduzindo perda de contexto entre gestão e entrega técnica | Não |
| Hierarquia madura de work items (Epic → Feature → PBI → Task) | Funciona bem para organizações maiores e cenários com portfólio, épicos e rastreabilidade mais complexa | Não |

---

## Pontos Negativos

| Ponto negativo | Impacto no usuário | Como evitar no nosso sistema |
|---|---|---|
| Priorização 100% manual, sem cálculo automático do que convém fazer primeiro | O time depende inteiramente de julgamento humano no momento da cerimônia, o que mantém subjetividade e pouca padronização na decisão | Manter no FitSprint um mecanismo explícito de priorização automática baseado em critérios e razão calculada |
| Não existe conceito nativo de buffer para imprevistos com alerta | O time precisa lembrar manualmente de reservar folga ou criar soluções improvisadas, aumentando risco de overcommitment | Preservar e evoluir o buffer com semáforo como elemento central do planejamento |
| Ferramenta ampla e relativamente pesada para o momento pontual do sizing | Durante a cerimônia, abrir e editar backlog em tempo real pode ser mais lento e menos focado do que uma interface dedicada | Manter o FitSprint enxuto e orientado especificamente ao momento de priorizar e montar o sprint |

---

## Modelo de Negócio e Acesso

**Modelo de acesso:** SaaS corporativo dentro do ecossistema Azure DevOps
**Público-alvo:** Times de desenvolvimento, produto e engenharia que gerenciam backlog, sprint e ciclo de entrega
**Mercado / Segmento:** Gestão ágil de software, ALM/DevOps corporativo

---

## Aprendizados para o Nosso Projeto

- A análise confirma que o cálculo automático de priorização por razão, combinado com buffer e semáforo, é um diferencial real do FitSprint e não uma duplicação do que Azure DevOps Boards já oferece.
- Vale incorporar a ideia de capacidade por desenvolvedor, em vez de depender apenas de capacidade global do time. Isso reforça diretamente a direção da feature pendente **Carga por desenvolvedor**.
- O FitSprint acerta ao não competir com hierarquia pesada de tracking (Epic/Feature/PBI/Task), porque esse problema já é bem atendido por ferramentas generalistas e não é o foco da cerimônia de sizing.
- Há espaço para posicionar o FitSprint como ferramenta complementar ao Jira/Azure DevOps: menos sobre rastrear tudo, mais sobre apoiar a decisão rápida e objetiva de o que cabe e o que entra primeiro no sprint.

---

## Referências Visuais

**Screenshot 1:** `Sprint Backlog` — visão do backlog por sprint/iteração com ordenação manual dos itens
**Screenshot 2:** `Sprints > Capacity` — painel de capacidade por pessoa com horas por dia e dias livres
**Screenshot 3:** `Taskboard` — quadro visual em tempo real com itens por estado durante o sprint
