# Personas do Projeto

## Visão Geral

Este documento consolida as principais personas de FitSprint para representar os três papéis centrais atualmente considerados no produto: quem conduz e fecha o sprint, quem participa do sizing e executa o trabalho priorizado, e quem administra a plataforma em contexto multi-equipe. As personas abaixo não são resultado de pesquisa formal com entrevistas registradas; elas foram construídas a partir da experiência direta do autor, observação de rituais reais de sizing e hipóteses explícitas de produto para papéis ainda não operacionalizados por interface.

---

## Persona 1 — Sofía, Scrum Master / Tech Lead

### Identificação

**Nome da persona:** Sofía
**Papel / Cargo:** Scrum Master / Tech Lead
**Área / Departamento:** Ingeniería / equipo ágil interno
**Baseada em:** Experiência direta do autor, que hoje exerce o papel de facilitador do sizing; sem entrevistas externas formais registradas

### Contexto

Sofía lidera um time ágil pequeno e usa FitSprint durante cerimônias colaborativas de sizing com tela compartilhada. Antes da reunião, organiza a agenda e os itens a discutir. Durante a sessão, registra bolinas com o time, acompanha a capacidade do sprint e observa o consumo de buffer em tempo real. Ao final do ciclo, fecha o sprint, ativa o seguinte e revisa os itens que ficaram de fora para possível replanejamento.

**Ferramentas que usa hoje:** Jira para tracking do trabalho; planilhas e post-its digitais para apoiar o sizing e as discussões de capacidade.
**Com quem interage:** Developers do time e Product Owner de forma indireta, principalmente via prioridades de negócio.

### Objetivos

- Ver a capacidade real do sprint em tempo real durante o sizing
- Evitar sobrecomprometer o time além do que consegue entregar
- Controlar quanto das urgências está consumindo o buffer e afetando o trabalho planejado

### Dores e Frustrações

- Com Jira ou planilhas, não há visibilidade ao vivo de quanto do sprint já foi preenchido
- É difícil perceber cedo quando os imprevistos estão consumindo o buffer previsto
- Ao encerrar o sprint, é fácil perder clareza sobre o que ficou fora e precisa ser reconsiderado no próximo ciclo

### Necessidades em Relação ao Sistema

- Saber em todo momento quanta capacidade livre ainda resta, sem depender de cálculo manual
- Perceber com antecedência quando urgências estão corroendo o buffer antes de comprometer a entrega planejada
- Encerrar e abrir sprints mantendo visibilidade clara sobre o que entrou, saiu ou ficou pendente

### Nível de Acesso e Permissões

| Módulo / Área | Tipo de acesso | Observação |
|---|---|---|
| Sizing e Sprints | Admin | Pode criar, editar, ativar e fechar sprints do seu time |
| Developers do time | Admin | Pode gerenciar developers do próprio time |
| Histórico e Calculadora | Escrita | Usa para consulta e operação do processo de priorização |
| Gestão de equipes e usuários globais | Sem acesso | Não pode administrar outras contas nem outros times |

### Citação Representativa

_"No quiero enterarme a mitad de sprint que ya prometimos más de lo que podemos entregar."_

### Entrevistas que Embasam esta Persona

Não existem entrevistas registradas para esta persona. Ela foi construída a partir da experiência direta do autor como Tech Lead e facilitador de sizings reais.

---

## Persona 2 — Martín, Developer

### Identificação

**Nome da persona:** Martín
**Papel / Cargo:** Developer
**Área / Departamento:** Desarrollo / miembro del equipo
**Baseada em:** Hipótese de produto e observação própria do autor sobre a participação de developers em sizings reais; sem entrevistas formais

### Contexto

Martín participa do sizing propondo tamanho e, quando aplicável, valor das tarefas que conhece melhor. Durante o sprint, consulta a tabela priorizada para entender onde deve focar e ocasionalmente revisa sua carga em relação ao resto do time. Ele precisa acompanhar a priorização sem perder tempo com processos manuais ou pouco transparentes durante reuniões ao vivo.

**Ferramentas que usa hoje:** Jira e o quadro operacional do time para acompanhar o próprio trabalho.
**Com quem interage:** Scrum Master / Tech Lead durante o sizing e outros developers no dia a dia do sprint.

### Objetivos

- Entender o que está sendo priorizado primeiro e por quê
- Evitar ficar sobrecarregado em comparação com o restante do time
- Conseguir registrar rapidamente insumos da tarefa durante reuniões de sizing ao vivo

### Dores e Frustrações

- Em outras ferramentas, a priorização tende a ser manual e subjetiva
- Falta um critério objetivo e visível que relacione valor e esforço
- Em cerimônias ao vivo, registrar ou ajustar tarefas com pouca fricção nem sempre é simples

### Necessidades em Relação ao Sistema

- Entender de forma transparente por que uma tarefa entra antes de outra na fila de priorização
- Visualizar quando uma urgência está deslocando o foco do que foi planejado para o sprint
- Conseguir registrar uma tarefa rapidamente no contexto de uma reunião em andamento, sem interromper o fluxo do time

### Nível de Acesso e Permissões

| Módulo / Área | Tipo de acesso | Observação |
|---|---|---|
| Sizing | Escrita limitada | Pode visualizar o sizing e registrar tamanho/bolinas e valor das tarefas, sem editar prioridades nem fechar sprint |
| Histórico | Leitura | Pode consultar histórico e tabela priorizada |
| Sprints | Sem acesso administrativo | Não pode criar, ativar ou fechar sprint |
| Administração de developers, equipes e usuários | Sem acesso | Não pode gerenciar cadastros nem configurações administrativas |

### Citação Representativa

_"Si me van a asignar algo, que sea porque tiene sentido, no porque cayó ahí porque sí."_

### Entrevistas que Embasam esta Persona

Não existem entrevistas reais registradas para esta persona. Ela foi construída como hipótese informada pela observação do autor em sizings reais.

---

## Persona 3 — Valentina, Super Admin

### Identificação

**Nome da persona:** Valentina
**Papel / Cargo:** Super Admin
**Área / Departamento:** Plataforma / operação da ferramenta
**Baseada em:** Hipótese de produto; hoje este papel é exercido pelo próprio autor via API, sem um usuário distinto nem entrevistas formais

### Contexto

Valentina representa o papel técnico-administrativo responsável por operar a plataforma em cenário multi-equipe. Quando um novo squad adota o FitSprint, ela cria o time, cadastra usuários, define papéis de acesso e audita ocasionalmente se o isolamento entre equipes está sendo respeitado. Atualmente, esse trabalho não ocorre por interface administrativa, mas por chamadas diretas à API.

**Ferramentas que usa hoje:** Swagger e chamadas diretas à API, pois ainda não existe UI administrativa.
**Com quem interage:** Tech Leads ou responsáveis de cada equipe que passa a usar a ferramenta.

### Objetivos

- Garantir que nenhum time visualize dados de outro
- Dar de alta e baixa usuários e developers sem depender de procedimentos manuais fora do sistema
- Operar a plataforma multi-equipe com segurança e baixo atrito

### Dores e Frustrações

- Hoje não existe painel administrativo por interface; toda a gestão ocorre via API
- A operação administrativa depende de conhecimento técnico e não é adequada para escala de adoção
- A ausência de UI aumenta o atrito para manutenção de usuários e times

### Necessidades em Relação ao Sistema

- Dar de alta um novo time sem tocar diretamente no banco de dados ou recorrer a processos manuais
- Confiar que o isolamento por equipe seja garantido sem necessidade de checagem manual constante
- Desativar, resetar ou ajustar usuários sem depender de scripts ou intervenção técnica externa

### Nível de Acesso e Permissões

| Módulo / Área | Tipo de acesso | Observação |
|---|---|---|
| Teams | Admin global | Pode criar, editar e remover equipes |
| Users | Admin global | Pode criar, editar, remover e atribuir papéis |
| Escopo transversal da plataforma | Acesso total | É o único papel não limitado por team_id |
| Operação funcional de sprint por equipe | Quando necessário | Não é o foco principal do papel; seu valor está na administração da plataforma |

### Citação Representativa

_"Mientras nadie vea lo que no le corresponde, no me importa cómo se vea la pantalla."_

### Entrevistas que Embasam esta Persona

Não existem entrevistas registradas para esta persona. Trata-se de uma hipótese de produto para um papel que ainda não possui usuário real distinto do autor e cuja operação hoje acontece via API.
