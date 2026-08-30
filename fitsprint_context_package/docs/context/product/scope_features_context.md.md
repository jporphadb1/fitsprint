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
| 5 | Filtros e vistas | Facilita a leitura operacional do sprint com visões por developer, estado, zona operacional e disponibilidade. |

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

Este módulo permite enxergar a distribuição real do trabalho no sprint ativo e apoiar decisões de alocação ou realocação com base em capacidade e tarefas já atribuídas. Ele é útil para ADMIN e USER do time, porque ambos podem atribuir, desatribuir e reatribuir tarefas, sem exclusividade administrativa. O valor é transformar o acompanhamento do sprint em uma leitura operacional simples, acionável e coerente com o estado real do sprint em cada consulta.

#### Feature: Mapa de alocação por developer

Mostra quem está trabalhando em quais tarefas dentro do sprint ativo, associando responsáveis e estados de execução em uma visão operacional por developer. A leitura considera apenas tarefas do sprint ativo que não estejam marcadas como fora nem eliminadas. Tarefas sem responsável continuam visíveis no sprint, mas não consomem capacidade de ninguém. A atribuição e reatribuição de tarefas pode ser feita tanto por ADMIN quanto por USER, desde que o responsável informado pertença ao mesmo time da tarefa.

#### Feature: Cálculo de ocupação por developer

A ocupação de cada developer é definida como a soma dos story points de todas as tarefas atribuídas a ele no sprint ativo, independentemente do estado da tarefa (TODO, IN_PROGRESS ou DONE), desde que a tarefa não esteja fora nem eliminada. Essa ocupação nunca é persistida: ela é recalculada em tempo de consulta a partir do conjunto atual de tarefas do sprint e da capacidade total configurada para cada developer. Com isso, qualquer alteração de atribuição, desatribuição, reatribuição, mudança de tamanho, marcação como fora ou exclusão deve se refletir automaticamente na próxima leitura, sem trigger ou processo separado de consolidação.

#### Feature: Indicação de sobrecarga e disponibilidade

O sistema deve identificar developers sem tarefas, developers com carga dentro da capacidade e developers sobrecarregados. A regra central é que o sistema não bloqueia atribuições que excedam a capacidade planejada; em vez disso, apenas sinaliza sobrecarga quando a ocupação calculada for maior que a capacidade total do developer. Se um developer não tiver capacidade configurada, a comparação é feita contra zero, de modo que qualquer tarefa atribuída já o coloca como sobrecarregado. Se não tiver tarefas atribuídas no sprint ativo, sua ocupação deve aparecer como zero.

#### Feature: Vista operacional limpa de alocação

A visão operacional de ocupação e atribuição não deve exibir tarefas eliminadas. Reatribuições devem aparecer como efeito natural do cálculo em runtime: a carga do developer anterior diminui e a do novo aumenta na próxima leitura, sem histórico intermediário nessa vista. O módulo é restrito ao sprint ativo e não oferece leitura histórica de ocupação nesta versão.

---

### Módulo: Buffer e urgências

Este módulo controla a reserva de capacidade para demandas não planejadas e dá visibilidade ao impacto das urgências no sprint. Ele atende especialmente ADMIN e USER do time no acompanhamento operacional, ao expor como trabalho incidental consome a capacidade reservada e pressiona o plano original do sprint. O valor é reduzir a ruptura silenciosa do sprint causada por urgências absorvidas sem controle.

#### Feature: Classificação operacional entre sprint normal e continuidade operativa

As tarefas do sprint ativo são separadas em duas zonas operativas calculadas exclusivamente pelo tipo de tarefa. A zona CONTINUIDAD_OPERATIVA inclui apenas URGENCIA e BUG_NUEVO. A zona SPRINT_NORMAL inclui BUG_PLANEADO e REQ_PLANEADO. Todo tipo de tarefa deve obrigatoriamente mapear para uma dessas duas zonas, cobrindo 100% do enum válido do sistema. O enquadramento da tarefa em uma zona nunca depende de tamanho, prioridade, estado ou existência de responsável.

#### Feature: Cálculo do buffer reservado e consumido

O buffer reservado em story points é calculado como ceiling(capacidade_total_sprint × %buffer), sempre com arredondamento para cima. O buffer consumido é a soma dos story points de todas as tarefas URGENCIA e BUG_NUEVO do sprint ativo que não estejam fora nem eliminadas, independentemente do estado da tarefa e mesmo que ela não tenha responsável. A saída operacional do módulo deve expor buffer reservado, buffer consumido e buffer disponível para leitura rápida do desvio em relação à reserva planejada.

#### Feature: Semáforo de consumo do buffer

O sistema deve expor um semáforo operacional de uso do buffer com a seguinte regra: verde quando o uso for menor que 50%, amarelo quando o uso estiver entre 50% e 100% inclusive, e vermelho quando ultrapassar 100%. O percentual de uso deve ser calculado a partir da relação entre buffer consumido e buffer reservado, exceto nos casos especiais em que o buffer reservado seja zero. Se buffer reservado = 0 e buffer consumido = 0, o sistema deve apresentar uso efetivo de 0% e semáforo verde. Se buffer reservado = 0 e buffer consumido > 0, o sistema não deve calcular percentual nem realizar divisão por zero; deve marcar diretamente semáforo vermelho.

#### Feature: Leitura operacional de desvio por urgências

O módulo deve tornar explícito quando o sprint está sendo pressionado por trabalho não planejado, mas sem automatizar replanejamento, notificação proativa ou qualquer ação corretiva automática. A responsabilidade do sistema nesta versão é medir e sinalizar o consumo do buffer reservado com precisão, inclusive em cenários de múltiplas urgências simultâneas, sprint sem urgências e percentuais decimais que exijam arredondamento do reservado.

---

### Módulo: Filtros e vistas

Este módulo organiza formas de leitura do sprint ativo para diferentes necessidades operacionais. Ele é usado por ADMIN e USER do time para navegar rapidamente entre recortes relevantes sem reconstruir mentalmente a situação do sprint. O valor entregue é tornar a informação acionável, consistente com as regras reais de priorização e fácil de consultar no dia a dia, sem permitir edição a partir dessas telas.

#### Feature: Vista principal da lista priorizada do sprint ativo

Existe uma única vista principal composta pela lista priorizada do sprint ativo, restrita por team_id e não por papel do usuário. Essa lista deve refletir a ordenação operacional sem alterar a prioridade persistida. O ordenamento visível segue a regra: primeiro prioridade final manual, quando existir; se não existir, ratio em ordem decrescente; em caso de empate, ordem de criação como critério de desempate. Tarefas com prioridade manual aparecendo acima de outras com ratio melhor são comportamento esperado, não inconsistência.

#### Feature: Filtros operativos combinados

A vista principal deve permitir filtros por developer, estado e zona, sempre combinados com lógica AND. O filtro de developer aceita apenas membros do mesmo time e a opção especial "Sin asignar" para tarefas sem responsável. O filtro de estado aceita exclusivamente os três valores reais do modelo: TODO, IN_PROGRESS e DONE. O filtro de zona aceita apenas CONTINUIDAD_OPERATIVA e SPRINT_NORMAL, substituindo o filtro antigo de prioridade, que não era implementável como filtro discreto por depender de ordenação contínua. Combinações sem resultado devem retornar lista vazia, nunca erro.

#### Feature: Vista de urgências

Além da lista principal, o módulo oferece uma vista derivada de urgências que mostra apenas tarefas classificadas como URGENCIA e BUG_NUEVO no sprint ativo. Essa visão existe para facilitar acompanhamento diário de trabalho incidental e leitura rápida do que está pressionando o buffer operacional.

#### Feature: Vista de disponibilidade por developer

O módulo também oferece uma vista derivada de disponibilidade que resume, por developer, capacidade total, ocupação atual, capacidade livre e quantidade de tarefas atribuídas. Developers sem tarefas devem aparecer com ocupação zero. Essa visão depende das regras de capacidade do sprint e do cálculo dinâmico de ocupação, servindo como apoio de leitura e não como ponto de edição.

#### Feature: Módulo estritamente de consulta

Este módulo não permite editar estado, prioridade, responsável ou qualquer outro dado operacional. Também não oferece reatribuição, priorização manual, guardado de vistas, exportação, visualização em kanban, filtro por fecha ou leitura cross-team. O objetivo é garantir uma superfície de consulta limpa e segura, focada em acompanhamento diário do sprint ativo.

---

## Fora do Escopo

| Item excluído | Motivo |
|---|---|
| Integração com Jira | A primeira versão foca na visibilidade e gestão interna do sprint sem dependência de integrações externas. |
| Replanejamento automático do sprint | As decisões de redistribuição e ajuste continuarão sendo manuais nesta fase inicial. |
| Estimativa de esforço com IA | O produto não fará previsão automática de esforço nesta primeira versão, mantendo o processo de estimativa com o time. |
| Priorização automática completa | Embora o sistema mostre ratio de valor versus esforço, a importância seguirá sendo definida manualmente por PO ou Scrum Master. |
| Edição massiva a partir de vistas operativas | As telas de leitura do sprint não terão ações em lote no MVP. |
| Modelagem avançada de calendários de capacidade | O cálculo de capacidade não considerará calendários complexos nesta primeira versão. |
| Guardado de vistas por usuário | As preferências de filtros e visualizações não serão persistidas no MVP. |
| Exportação e relatórios históricos | O foco inicial é leitura operacional do sprint ativo, sem histórico analítico. |
| Vista kanban ou board | O módulo de vistas operativas ficará restrito à lista priorizada, urgências e disponibilidade. |
| Notificação proativa de desvio de buffer | O sistema apenas expõe o semáforo operacional, sem alertas automáticos push no MVP. |