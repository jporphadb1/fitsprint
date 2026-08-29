# Gestão do Projeto e Ciclo de Desenvolvimento

## Plataforma de Gestão

**Plataforma:** GitHub (repositório do projeto)
**URL / Acesso:** github.com/jporpha/fitsprint
**Como solicitar acesso:** Não se aplica. O FitSprint é um projeto individual mantido no repositório pessoal do autor, sem processo de onboarding para terceiros.

---

## Modelo de Organização do Trabalho

Como o FitSprint não utiliza board formal nem ferramenta de gestão como Jira ou Azure DevOps, não existe uma hierarquia estruturada de trabalho com épicos, features, histórias e subtarefas. O backlog é mantido de forma informal na seção “Lo que quedó pendiente” do README do repositório.

| Nível | Nome utilizado | O que representa | Exemplo |
|---|---|---|---|
| 1 — mais alto | Feature pendente | Item funcional anotado informalmente como próxima evolução do produto | Melhorar visualização da priorização do sprint |
| 2 | Não utilizado | Não há agrupadores intermediários formais | Não se aplica |
| 3 | Não utilizado | Não há PBIs, stories ou tasks registradas em board | Não se aplica |
| 4 — mais baixo | Não utilizado | Não há subtarefas formais | Não se aplica |

---

## Tamanho e Critérios de um PBI

Mesmo sem PBI formal, a regra prática usada pelo autor é de escopo curto por sessão.

**Tamanho máximo:** Uma tarefa ou feature deve poder ser implementada e validada dentro de uma única sessão de desenvolvimento, normalmente em poucas horas. Como regra adicional de recorte do MVP, se algo não cabe no tempo disponível de um fim de semana de trabalho, não entra naquela iteração.

**Um bom PBI deve:**
- Ter escopo pequeno o suficiente para ser concluído individualmente sem depender de coordenação externa
- Poder ser validado em uso real logo após o deploy
- Produzir avanço concreto no produto sem exigir estrutura formal de acompanhamento
- Ser priorizado de acordo com o valor imediato para a evolução do MVP

**Um PBI deve ser quebrado quando:**
- Não couber em uma única sessão de desenvolvimento
- Exigir mais tempo do que o disponível para a iteração prática do fim de semana
- O escopo estiver grande demais para ser implementado e validado rapidamente
- Houver necessidade de recortar o MVP para preservar velocidade de entrega

Observação: no contexto real do projeto, essa quebra não gera subtarefas formais em board; o mais comum é recortar o escopo ou adiar parte da feature para uma sessão futura.

---

## Modelo de Desenvolvimento

**Metodologia:** Desenvolvimento individual orientado por sessões, sem adoção formal de Scrum, Kanban ou Scrumban

**Duração do ciclo:** Não há ciclo fixo. O trabalho acontece em sessões curtas de implementação, frequentemente agrupadas em fins de semana

**Início do ciclo:** Não há data fixa. Cada sessão começa conforme disponibilidade do autor e prioridade percebida para o MVP

---

## Cerimônias e Rituais

O projeto não possui cerimônias formais, pois não há equipe, stakeholders externos nem necessidade de sincronização operacional.

| Cerimônia | Frequência | Duração | Objetivo |
|---|---|---|---|
| Não se aplica | — | — | O desenvolvimento é individual e não segue rituais formais como planning, daily, review, retrospectiva ou refinamento |

---

## Fluxo de Status

Não existe fluxo de status de desenvolvimento configurado em ferramenta de gestão. O backlog é informal e controlado diretamente pelo autor.

| Status | Descrição | Quem move para cá |
|---|---|---|
| Pendente no README | Ideias, melhorias ou funcionalidades ainda não implementadas, registradas informalmente no repositório | O próprio autor |
| Em desenvolvimento | Item sendo implementado em uma sessão ativa de trabalho | O próprio autor |
| Em produção | Feature já deployada na Railway e validada em uso real | O próprio autor |

Observação: o sistema possui também um fluxo de status de domínio da entidade Bolina (TODO → IN_PROGRESS → DONE), mas esse fluxo pertence à operação da aplicação e não ao gerenciamento do desenvolvimento do produto.

---

## Definição de Pronto (Definition of Done)

No FitSprint, a definição de pronto é prática e informal, coerente com a natureza de projeto individual.

Um item só pode ser considerado concluído quando:
- Foi implementado pelo autor
- Foi deployado em produção na Railway
- Funciona corretamente em uso real
- Já pode ser utilizado em uma cerimônia de sizing sem falhas impeditivas

Não fazem parte da Definition of Done atual:
- Code review por terceiros, pois não existe outro desenvolvedor no projeto
- Checklist formal de QA
- Aprovação de PO externo
- Cerimônias de validação com equipe

---

## Acompanhamento e Monitoramento

**Responsável pelo acompanhamento:** O próprio autor, acumulando os papéis de desenvolvedor, gestor do produto e principal stakeholder.

**Métricas acompanhadas:** Atualmente não há métricas formais de gestão de desenvolvimento, como velocity, lead time ou taxa de bugs do processo.

| Métrica | O que mede | Onde é acompanhada | Frequência |
|---|---|---|---|
| Nenhuma métrica formal | Não se aplica | Não se aplica | Não se aplica |

**Reporte para stakeholders:** Não há reporte formal, pois o projeto não possui stakeholders externos nem equipe adicional. O acompanhamento ocorre de forma direta pelo próprio autor, com base no estado real da aplicação em produção e na lista de pendências mantida no README.