# Objetivo do Projeto

## Identificação do Sistema

**Nome do sistema:** fistSprint

**Status:** Em desenvolvimento (existe um protótipo inicial já programado)

**Repositório de código:** Não informado

**Última atualização:** 2026-08-02 — Makuco

### Ambientes

| Ambiente | URL |
|---|---|
| Desenvolvimento | Não informado |
| Homologação | Não informado |
| Produção | Não informado |

---

## Problema a Ser Resolvido

**Situação atual:** Hoje o Scrum Master precisa decidir manualmente quais tarefas os developers devem assumir ao longo do sprint, mas não existe uma visão consolidada das tarefas disponíveis, do esforço de cada uma, de quem pode assumi-las e da capacidade restante do time. Quando um developer termina uma tarefa, ele precisa perguntar ao Scrum Master o que deve pegar em seguida. Isso torna a priorização reativa, centralizada e pouco visível para o restante da equipe.

**Causa raiz:** O time não possui um sistema estruturado para visualizar backlog do sprint, capacidade disponível por developer e buffer reservado para urgências em um único lugar. Além disso, a priorização depende de uma lógica de negócio baseada em razão entre esforço e importância, mas essa lógica não está operacionalizada em uma ferramenta de apoio à decisão.

**Impacto:** O Scrum Master perde tempo fazendo mediação manual contínua. Os developers ficam dependentes de orientação direta para saber o que pegar a seguir. O time perde fluidez na execução do sprint, tem menor previsibilidade sobre capacidade restante e corre mais risco de desorganização quando surgem urgências. A falta de visibilidade também dificulta equilibrar esforço entre perfis diferentes, já que a capacidade é dinâmica por developer e medida em story points.

---

## Objetivo do Projeto

**Onde devemos chegar com o projeto entregue:**

- Permitir que o time visualize, em um único sistema, as tarefas do sprint ordenadas por prioridade calculada a partir da razão entre importância e esforço.
- Dar visibilidade contínua da capacidade disponível do time e de cada developer em story points, respeitando diferenças de senioridade e ritmo de entrega.
- Mostrar o percentual da capacidade do sprint reservado para urgências, medido em story points, e seu consumo ao longo do sprint.
- Fazer com que, ao concluir uma tarefa, cada developer consiga identificar com clareza qual item pode assumir em seguida sem depender sempre de decisão manual do Scrum Master.

---

## Visão Geral do Sistema

### Propósito

O fistSprint é um sistema de apoio à priorização de tarefas de sprint para equipes de desenvolvimento ágil. Seu objetivo é organizar a tomada de decisão sobre quais tarefas devem ser executadas a seguir, com base em esforço, importância, capacidade disponível e buffer reservado para urgências. O sistema busca reduzir dependência operacional do Scrum Master e dar mais autonomia ao time para manter o fluxo de trabalho organizado durante o sprint.

### Público-Alvo e Usuários

**Perfil 1 — Scrum Master**  
Descrição: responsável por acompanhar o andamento do sprint, visualizar capacidade restante e orientar a priorização das tarefas. Hoje atua como ponto central de decisão quando um developer finaliza uma entrega e precisa saber o que fazer em seguida.  
O que faz e quando faz: consulta o estado geral do sprint, acompanha ocupação do time, verifica buffer disponível e usa a priorização para orientar a próxima tarefa a ser assumida.

**Perfil 2 — Developer**  
Descrição: membro do time de desenvolvimento, com capacidade variável conforme senioridade e ritmo de entrega. Utiliza o sistema para entender quais tarefas estão disponíveis e adequadas para assumir dentro da capacidade do sprint.  
O que faz e quando faz: ao longo do sprint, especialmente após concluir uma tarefa, consulta a lista priorizada e a capacidade disponível para decidir qual item pegar em seguida.

**Perfil 3 — PO / Tech Lead**  
Descrição: perfis que acompanham o andamento do sprint e influenciam ou validam a importância das tarefas priorizadas. São stakeholders relevantes para alinhamento entre valor de negócio e execução técnica.  
O que faz e quando faz: acompanham a priorização, observam o andamento do sprint e consultam a organização do trabalho para apoiar decisões sobre sequência de execução e urgências.

### Contexto de Mercado e Posicionamento

**Contexto de mercado:** O sistema atua no contexto de gestão ágil de times de desenvolvimento de software, especialmente em operações que trabalham com sprint, backlog e distribuição contínua de tarefas entre developers.

**Posicionamento:** O diferencial do sistema é combinar priorização orientada por valor versus esforço com visibilidade de capacidade por developer e buffer de urgências, ajudando o time a decidir não apenas o que é mais importante, mas também o que efetivamente pode ser absorvido dentro do sprint.

**Público-alvo de mercado:** Equipes de desenvolvimento ágil que precisam organizar melhor a tomada de tarefas durante o sprint, especialmente em cenários com múltiplos developers, prioridades concorrentes e capacidade desigual entre membros do time.

### Contexto de Uso pelo Cliente

O sistema será usado no dia a dia da operação do time de desenvolvimento para organizar a execução do sprint. Sempre que um developer concluir uma tarefa, poderá consultar o sistema para identificar o próximo item elegível conforme prioridade e capacidade. O sistema apoia o processo hoje centralizado no Scrum Master e, na primeira versão, não terá integração com Azure DevOps, então a carga de tarefas deverá ocorrer por meio ainda não automatizado ou manual.

---

## Contexto de Negócio

**Sobre o negócio:** O projeto nasce de uma necessidade operacional real do time de desenvolvimento: reduzir a dependência do Scrum Master como intermediador constante da priorização e melhorar a organização do sprint.

**Domínio e segmento:** Gestão de trabalho ágil em times de desenvolvimento de software, com foco em priorização de tarefas de sprint e alocação de capacidade.

**Processo atual (como as pessoas fazem hoje):** Atualmente não existe um processo apoiado por sistema para recomendar a próxima tarefa. Quando um developer termina uma atividade, pergunta diretamente ao Scrum Master o que pode assumir a seguir. A decisão depende de leitura manual do contexto, sem visão consolidada de esforço, importância, capacidade disponível e buffer.

**Restrições e regras de negócio relevantes:** A priorização das tarefas é baseada em uma razão entre esforço exigido pela tarefa e seu valor de importância. Tanto esforço quanto importância são avaliados com escala Fibonacci. A capacidade do time é tratada de forma dinâmica por developer, em story points, considerando que um perfil júnior não entrega no mesmo ritmo que um sênior. O buffer representa um percentual da capacidade do sprint reservado para absorver urgências durante o sprint, medido em story points. A integração com Azure DevOps foi considerada importante, mas está fora do escopo da primeira versão.

---

## Escopo Macro do Projeto

| # | Módulo / Epic | Prioridade |
|---|---|---|
| 1 | Visualização de tarefas do sprint ordenadas por ratio de prioridade | Alta |
| 2 | Visualização da capacidade do time e por developer | Alta |
| 3 | Acompanhamento de ocupação e andamento das tarefas | Alta |
| 4 | Visualização e controle de buffer para urgências | Média |

---

## Escopo Negativo do Projeto

| O que não será feito | Motivo |
|---|---|
| Integração com Azure DevOps na primeira versão | Importante para evolução futura, mas fora do escopo inicial para reduzir complexidade |
| Automação completa da captura de tarefas do sprint na primeira versão | A primeira entrega foca em visibilidade e apoio à decisão antes de integrações externas |
| Equalização de produtividade entre developers por regras fixas | A capacidade é dinâmica por developer e não deve ser tratada como uniforme |

---

## Pessoas e Interesses (Stakeholders)

| Nome | Empresa / Área | Papel no Projeto |
|---|---|---|
| Scrum Master | Time ágil / operação do sprint | Usuário principal e responsável atual pela priorização |
| PO | Produto / negócio | Stakeholder de negócio e influência na importância das tarefas |
| Tech Lead | Liderança técnica | Stakeholder técnico e apoio à priorização |
| Dev 1 a Dev 7 | Desenvolvimento | Usuários finais que consultam e executam as tarefas priorizadas |

---

> **Próximo passo:** com este documento preenchido e revisado, acione o `makuco-specify` referenciando este arquivo para gerar as specs de cada módulo listado no Escopo Macro.