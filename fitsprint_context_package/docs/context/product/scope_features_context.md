# Detalhamento do Escopo Macro do Projeto

## Visão Geral do Produto

fistSprint é um sistema enxuto para dar visibilidade real do sprint, centralizando a priorização do backlog e a capacidade do time em um único lugar. O problema principal hoje é a falta de clareza sobre quais tarefas devem ser atacadas primeiro e sobre quem está sobrecarregado ou com disponibilidade. Quando o projeto estiver completo, Scrum Master, PO e developers deverão conseguir entender rapidamente a prioridade das tarefas, a ocupação do time, os riscos de carga e o consumo do buffer de urgências para tomar decisões de planejamento e execução com mais organização e melhor uso da capacidade ao longo dos sprints.

---

## Roadmap

| Ordem | Módulo | O que entrega ao negócio |
|---|---|---|
| 1 | Backlog priorizado | Dá visibilidade sobre quais tarefas têm maior prioridade e melhor relação entre valor e esforço, apoiando a ordem de execução do sprint. |
| 2 | Capacidade do time | Mostra a capacidade total, usada e disponível por developer, reduzindo alocações desequilibradas. |
| 3 | Ocupação e avanço | Permite enxergar quem está trabalhando em quê, quem está sobrecarregado e quem ficará disponível em breve. |
| 4 | Buffer e urgências | Controla a reserva de capacidade para urgências e alerta quando esse buffer está sendo consumido. |
| 5 | Filtros e vistas | Facilita a leitura operacional do sprint com visões por developer, prioridade, estado e disponibilidade. |

---

## Módulos e Features

---

### Módulo: Backlog priorizado

Este módulo organiza as tarefas do sprint conforme sua importância para o negócio e seu esforço estimado. É usado principalmente por PO e Scrum Master para decidir a ordem de ataque do backlog, mas também orienta os developers sobre o que deve ser executado primeiro. O valor principal é reduzir a ambiguidade de prioridade dentro do sprint.

#### Feature: Visualização de tarefas priorizadas

Exibe a lista de tarefas do sprint com os principais campos visíveis para tomada de decisão: título, story points, importância, ratio, estado e responsável. A feature deve permitir que o time compreenda rapidamente o peso relativo de cada tarefa sem depender de leitura detalhada de múltiplas fontes. O objetivo não é apenas listar tarefas, mas oferecer uma visão funcional que ajude a ordenar o trabalho do sprint.

#### Feature: Classificação manual de importância

Permite que PO ou Scrum Master definam manualmente a importância de cada tarefa usando níveis como alta, média e baixa. Nesta primeira versão, a importância não será calculada automaticamente pelo sistema; ela reflete uma decisão operacional do time sobre valor e prioridade percebida. Essa regra diferencia o produto de um priorizador puramente matemático e mantém o controle da decisão com os papéis de gestão do sprint.

#### Feature: Cálculo de ratio valor versus esforço

Apresenta para cada tarefa um ratio que representa quanto valor ela entrega ao negócio em comparação com o esforço necessário para executá-la. Esse indicador serve como apoio complementar à importância manual, ajudando o time a perceber tarefas com boa relação custo-benefício. A feature não substitui a decisão do PO ou Scrum Master, mas oferece um critério adicional para ordenar o backlog de forma mais consciente.

---

### Módulo: Capacidade do time

Este módulo mostra a capacidade operacional do sprint por developer e consolida a leitura do uso do time. Ele atende Scrum Master e PO na distribuição de trabalho e ajuda os developers a entenderem sua disponibilidade. O valor entregue é evitar sobrecarga invisível e melhorar o aproveitamento da capacidade durante o sprint.

#### Feature: Capacidade total, usada e disponível por developer

Exibe para cada developer sua capacidade total planejada, a parcela já utilizada e a capacidade ainda disponível. A visualização deve ser individual, e não apenas agregada em nível de equipe, porque a principal dor atual inclui não saber quem está sobrecarregado e quem pode absorver novas tarefas. A regra de negócio central é que a capacidade disponível deve ser mostrada explicitamente por pessoa para apoiar redistribuições no sprint.

#### Feature: Consolidação de capacidade da equipe

Além da leitura individual, o sistema deve consolidar a capacidade do time para permitir uma visão geral do sprint. Essa consolidação ajuda PO e Scrum Master a entender se o conjunto da equipe ainda comporta novas tarefas ou urgências. O módulo deve preservar a relação entre visão macro do time e detalhe por developer, sem esconder os desequilíbrios internos.

---

### Módulo: Ocupação e avanço

Este módulo permite enxergar a distribuição real do trabalho em andamento durante o sprint. Ele é útil para Scrum Master, PO e developers ao mostrar quem está executando cada tarefa, quem está com carga excessiva e quem deve liberar capacidade em breve. O valor é transformar o acompanhamento do sprint em uma leitura operacional simples e acionável.

#### Feature: Mapa de alocação por developer

Mostra quem está trabalhando em quais tarefas dentro do sprint, associando responsáveis e estados de execução. A feature deve facilitar a leitura do trabalho em andamento por pessoa, permitindo identificar concentração excessiva de tarefas ou dependência de poucos developers. Essa visão ajuda o time a agir antes que o desequilíbrio vire atraso.

#### Feature: Indicação de sobrecarga e disponibilidade próxima

Destaca quais developers estão sobrecarregados e quais tendem a ficar livres em breve, com base na ocupação observada. Nesta primeira versão, o sistema deve representar ocupação e avanço de forma funcional o suficiente para orientar a tomada de decisão, mesmo sem automação avançada de replanejamento. A regra importante é que a visualização precisa apoiar o balanceamento manual do sprint, e não apenas reportar status históricos.

#### Feature: Sinalização de risco por carga

O sistema deve marcar tarefas ou pessoas como em risco quando a carga ultrapassar um nível considerado excessivo pelo time. Essa sinalização é relevante porque o produto precisa expor problemas de alocação antes que se convertam em bloqueios ou atrasos. Mesmo que os limiares exatos ainda possam ser ajustados depois, a feature deve existir como mecanismo de alerta operacional na primeira versão.

---

### Módulo: Buffer e urgências

Este módulo controla a reserva de capacidade para demandas não planejadas e dá visibilidade ao impacto das urgências no sprint. Ele atende especialmente Scrum Master e PO, mas também ajuda os developers a entender quando o plano está sendo pressionado por trabalho emergencial. O valor é reduzir a ruptura silenciosa do sprint causada por urgências absorvidas sem controle.

#### Feature: Visualização do buffer reservado

Mostra a quantidade de capacidade reservada para urgências, o quanto já foi consumido e o que ainda resta disponível. A leitura do buffer deve ser simples e direta para apoiar decisões rápidas durante o sprint. Essa feature materializa uma prática de planejamento que costuma ficar implícita ou dispersa, tornando o impacto das urgências visível para o time.

#### Feature: Alerta de consumo do buffer

Gera alerta quando uma urgência consome o buffer reservado do sprint. O objetivo não é replanejar automaticamente, mas sinalizar que a reserva está sendo usada e que o time pode precisar rever prioridades manualmente. Essa regra é importante porque o produto, nesta primeira versão, deve informar a pressão do trabalho urgente sem automatizar decisões de planejamento.

---

### Módulo: Filtros e vistas

Este módulo organiza formas de leitura do sprint para diferentes necessidades operacionais. Ele é usado por todos os perfis do sistema para navegar rapidamente entre recortes relevantes sem reconstruir mentalmente a situação do time. O valor entregue é tornar a informação acionável e fácil de consultar no dia a dia.

#### Feature: Filtros por developer, prioridade e estado

Permite filtrar a visualização do sprint por developer responsável, nível de prioridade e estado das tarefas. Essa feature é necessária porque o sistema deve servir tanto à visão geral do time quanto à leitura específica de cada recorte operacional. A regra de negócio é que os filtros devem responder às perguntas mais frequentes do acompanhamento diário, sem exigir navegação complexa.

#### Feature: Vistas por urgência e disponibilidade

Oferece visões específicas para identificar tarefas urgentes, capacidade livre e distribuição de carga. Isso ajuda o time a localizar rapidamente oportunidades de realocação ou focos de atenção sem precisar interpretar toda a base de tarefas. A intenção é apoiar o uso cotidiano do produto como instrumento de decisão, e não apenas como painel estático.

---

## Fora do Escopo

| Item excluído | Motivo |
|---|---|
| Integração com Jira | A primeira versão foca na visibilidade e gestão interna do sprint sem dependência de integrações externas. |
| Replanejamento automático do sprint | As decisões de redistribuição e ajuste continuarão sendo manuais nesta fase inicial. |
| Estimativa de esforço com IA | O produto não fará previsão automática de esforço nesta primeira versão, mantendo o processo de estimativa com o time. |
| Priorização automática completa | Embora o sistema mostre ratio de valor versus esforço, a importância seguirá sendo definida manualmente por PO ou Scrum Master. |