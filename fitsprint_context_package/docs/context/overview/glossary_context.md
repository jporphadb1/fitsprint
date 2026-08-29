# Glossário do Projeto

## Termos do Domínio

| Termo | Tradução EN | Definição | Evitar (sinônimos incorretos) |
|---|---|---|---|
| Sprint | Sprint | Período de trabalho em que o time seleciona e executa tarefas planejadas. No contexto do fistSprint, o sprint é a unidade principal de acompanhamento de prioridade, capacidade, ocupação e buffer. É usado por PO, Scrum Master e developers para decidir o que cabe no ciclo atual e como distribuir o trabalho. | Iteração, período genérico |
| Tarefa | Task | Unidade de trabalho tratada pelo sistema. Na prática, corresponde a uma card usada no Azure DevOps e representa um item que pode ser priorizado, estimado, atribuído e acompanhado dentro do sprint. Cada tarefa possui esforço em story points, importância para o negócio, prioridade final, estado e responsável. | Ticket, issue, requerimento |
| Story points | Story points | Medida de esforço ou dificuldade de uma tarefa. No fistSprint, os story points são atribuídos pelo time de developers junto com o tech lead, que neste contexto também é considerado um developer. A estimativa usa escala Fibonacci e serve como base para cálculo de capacidade e do ratio. | Horas, tempo exato |
| Importância | Business importance | Medida de valor de negócio percebido de uma tarefa. É definida em conjunto pelo PO e pelo time, também em escala Fibonacci. A importância expressa quanto a entrega daquela tarefa é relevante para o negócio, mas não define sozinha a ordem final de execução. | Prioridade |
| Ratio | Ratio | Indicador usado para ordenar inicialmente as tarefas com base na relação entre importância e dificuldade. Seu objetivo é favorecer tarefas com alta importância e baixo esforço, ajudando a identificar o melhor custo-benefício para o sprint. O ratio gera uma ordenação inicial automática, mas essa ordenação pode ser alterada pela prioridade definida pelo PO. | Prioridade final |
| Prioridade | Priority | Ordem final de execução da tarefa dentro do sprint. É definida manualmente pelo PO e prevalece sobre a ordenação automática sugerida pelo ratio. Isso significa que uma tarefa pode subir ou descer na lista mesmo que seu ratio não a coloque entre as primeiras posições. | Importância, ratio |
| Capacidade | Capacity | Quantidade total de story points que um time consegue assumir em um sprint. No sistema, a capacidade é usada para comparar o volume planejado com o volume já alocado e o que ainda resta disponível. É uma medida operacional do quanto o time consegue absorver no ciclo atual. | Disponibilidade total sem contexto |
| Buffer | Buffer | Parcela da capacidade do sprint reservada para bugs ou urgências não planejadas. No fistSprint, o buffer é variável por sprint, ou seja, o time pode ajustar a reserva conforme o contexto. O sistema deve mostrar quanto foi reservado, quanto já foi consumido e quanto ainda resta. | Folga genérica |
| Urgência | Urgency | Indicador do quão urgente é uma tarefa. No contexto atual, a urgência é classificada como alta, média ou baixa e ajuda a sinalizar pressão operacional sobre o sprint, especialmente quando há consumo do buffer. | Prioridade automática |
| Backlog | Backlog | Conjunto de tarefas que precisam ser acompanhadas e priorizadas pelo time. No fistSprint, o backlog observado é o recorte de tarefas relevantes para o sprint, servindo de base para ordenação, alocação e acompanhamento operacional. | Lista solta de itens |
| Developer | Developer | Pessoa responsável por desenvolver as tarefas do sprint. No contexto do fistSprint, developer é quem recebe trabalho, contribui para estimar story points e tem sua capacidade, ocupação e disponibilidade monitoradas no sistema. O tech lead, para este contexto, também é tratado como developer. | Recurso |
| PO | Product Owner | Papel responsável por definir a prioridade final das tarefas. O PO também participa, junto com o time, da avaliação de importância para o negócio. No fistSprint, a principal autoridade sobre a ordem final da lista é o PO, mesmo quando ela diverge da ordenação por ratio. | Cliente |
| Scrum Master | Scrum Master | Papel que acompanha o andamento do sprint, ajuda a distribuir melhor o trabalho e usa o sistema para visualizar carga, capacidade, riscos e consumo de buffer. No fistSprint, o Scrum Master atua mais como facilitador operacional da execução do sprint. | Gestor genérico |
| Ocupação | Workload allocation | Quantidade de trabalho que um developer já tem atribuída dentro do sprint. A ocupação é usada para entender quem está com carga elevada, quem pode receber novas tarefas e onde há desequilíbrio de distribuição. | Status genérico |
| Disponibilidade | Availability | Quantidade de capacidade ainda livre para receber novas tarefas no sprint. É um indicador operacional importante para PO, Scrum Master e developers decidirem se ainda cabe trabalho adicional ou se será necessário redistribuir atividades. | Tempo livre informal |
| Sobrecarga | Overload | Sinalização de que um developer está acima de um nível considerado saudável de carga dentro do sprint. No contexto atual, a sobrecarga é tratada como um flag operacional para facilitar a identificação rápida de desequilíbrios. | Ocupação normal |
| Risco por carga | Load risk | Flag que indica risco operacional decorrente de excesso de carga atribuída a uma pessoa ou conjunto de tarefas. Serve para alertar antecipadamente sobre possíveis atrasos, gargalos ou má distribuição de trabalho. | Bloqueio |
| Responsável | Assignee | Developer associado a uma tarefa específica dentro do sprint. O responsável é usado para relacionar backlog, ocupação e capacidade individual, permitindo entender quem está executando cada item. | Dono genérico |
| Tech lead | Tech lead | Referência técnica do time que participa da estimativa de esforço das tarefas. Neste produto, para fins de capacidade e alocação, o tech lead é tratado como developer. | Papel externo ao time |

---

## Status e Ciclos de Vida

### Tarefa

A tarefa representa uma card de trabalho dentro do sprint. Seu ciclo de vida acompanha a execução operacional do item, desde o momento em que ainda não foi iniciado até sua conclusão. O fluxo também contempla impedimentos temporários.

| Status | Descrição | Transições permitidas |
|---|---|---|
| Por fazer | Tarefa já conhecida no sprint, mas ainda não iniciada. Pode estar priorizada e estimada, aguardando início. | En progreso |
| En progreso | Tarefa em execução por um developer. Já consome capacidade e compõe a ocupação do responsável. | Bloqueada, Hecha |
| Bloqueada | Tarefa iniciada, mas temporariamente impedida por alguma dependência, problema ou restrição. | En progreso, Hecha |
| Hecha | Tarefa concluída no sprint. Não deve mais consumir capacidade operacional ativa. | Sem transições |

---

## Relações Entre Termos

- Um sprint contém tarefas que serão acompanhadas, priorizadas e executadas dentro daquele ciclo.
- Uma tarefa corresponde a uma card do Azure DevOps.
- Uma tarefa possui story points, que representam seu esforço ou dificuldade.
- Uma tarefa possui importância de negócio, definida em conjunto pelo PO e pelo time.
- O ratio relaciona importância e story points para gerar uma ordenação inicial automática.
- A prioridade é definida manualmente pelo PO e pode sobrescrever a ordem sugerida pelo ratio.
- Um developer pode ser responsável por uma ou mais tarefas dentro do sprint.
- A ocupação de um developer é formada pelo conjunto de tarefas que já lhe foram atribuídas.
- A disponibilidade de um developer depende da diferença entre sua capacidade e sua ocupação atual.
- O buffer consome parte da capacidade total do sprint para reservar espaço para bugs e urgências não planejadas.
- Uma urgência pode consumir o buffer reservado do sprint.
- Sobrecarga e risco por carga são flags usados para sinalizar problemas de distribuição ou excesso de trabalho.
- O Scrum Master e o PO usam backlog, capacidade, ocupação e buffer para tomar decisões de planejamento e ajuste do sprint.

---

## Siglas e Abreviações

| Sigla | Significado | Contexto de uso |
|---|---|---|
| PO | Product Owner | Papel que define a prioridade final das tarefas e participa da avaliação de importância de negócio. |
| SM | Scrum Master | Papel que acompanha a execução do sprint, a distribuição de carga e o consumo de buffer. |
| SP | Story Points | Unidade usada para estimar esforço ou dificuldade das tarefas. |

---

## Histórico de Alterações

| Data | Termo | Alteração | Motivo |
|---|---|---|---|
| 2026-08-19 | Tarefa | Adicionado | Formalizar que a unidade de trabalho do sistema corresponde a uma card do Azure DevOps e evitar sinônimos ambíguos. |
| 2026-08-19 | Prioridade / Importância / Ratio | Adicionado | Registrar a diferença entre valor de negócio, ordenação automática e ordem final definida pelo PO. |
| 2026-08-19 | Ocupação | Adicionado | Definir o conceito operacional como quantidade de trabalho já atribuída a um developer dentro do sprint. |