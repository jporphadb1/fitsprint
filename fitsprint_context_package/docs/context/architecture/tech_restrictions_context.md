# Restrições e Decisões Técnicas

## Tecnologias Proibidas

Como o FitSprint é um projeto individual, enxuto e orientado à simplicidade operacional, devem ser evitadas tecnologias, bibliotecas, frameworks ou abordagens que aumentem complexidade sem ganho proporcional.

- Plataformas de orquestração complexas como Kubernetes
- Arquiteturas distribuídas como microservices
- Filas, brokers e mensageria sem necessidade comprovada
- Soluções enterprise que exijam operação dedicada
- Ferramentas que imponham custo recorrente desnecessário ao projeto

---

## Restrições Obrigatórias

- O sistema deve continuar simples de manter por um único desenvolvedor
- O deploy deve permanecer compatível com o modelo atual em Railway
- As decisões técnicas devem priorizar rapidez de implementação e baixo custo operacional
- Novas dependências devem ser avaliadas pelo impacto em manutenção, curva de aprendizado e complexidade de operação

---

## Decisões Técnicas Já Tomadas

| Decisão | Status | Contexto / Justificativa |
|---|---|---|
| Arquitetura em monólito em camadas | Definida | Compatível com o estágio atual do produto e com a realidade de desenvolvimento individual |
| Desenvolvimento por um único autor | Definida | Não existe equipe formal nem necessidade de coordenação entre múltiplos desenvolvedores |
| Ausência de plataforma formal de gestão de projeto | Definida | O backlog é mantido de forma informal no README do repositório, suficiente para o estágio atual |
| Priorização direta pelo autor | Definida | O escopo de cada sessão é definido sem processo formal de refinamento, planejamento ou board |
| Critério informal de conclusão | Definida | Uma feature é considerada pronta quando está em produção na Railway e funcionando em uso real |

---

## Trade-offs Assumidos

| Decisão / Restrição | Benefício obtido | Custo / Risco aceito |
|---|---|---|
| Não usar ferramenta formal de gestão | Menor overhead de processo e maior agilidade individual | Menor rastreabilidade histórica e pouca visibilidade estruturada do backlog |
| Não adotar Scrum/Kanban no desenvolvimento do produto | Liberdade para construir por sessões curtas e iterativas | Menor previsibilidade de entrega e ausência de cadência operacional formal |
| Não ter checklist formal de Done | Menor burocracia para evolução rápida | Maior dependência do julgamento individual do autor sobre qualidade e completude |
| Centralizar decisões no autor | Rapidez na tomada de decisão | Risco de viés individual e baixa redundância de conhecimento |

---

## Limites para Evolução Futura

As restrições atuais refletem o estágio presente do FitSprint, mas devem ser revisitadas caso o produto evolua para um contexto com equipe maior, múltiplos stakeholders ou necessidade de governança mais formal.

Sinais de revisão futura:
- Entrada de novos desenvolvedores no projeto
- Necessidade de organizar backlog com rastreabilidade
- Crescimento do número de funcionalidades e integrações
- Exigência de previsibilidade, métricas ou reporte para terceiros
- Aumento da criticidade operacional da aplicação
