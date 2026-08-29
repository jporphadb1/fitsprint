# Stack de Tecnologia

## Linguagem e Runtime

| Item | Tecnologia | Versão | Observação |
|---|---|---|---|
| Linguagem principal | Java | 17 | Aplicação backend principal do fistSprint |
| Runtime / Plataforma | JVM | 17 | Execução da aplicação Spring Boot |
| Gerenciador de pacotes | Maven | Wrapper do projeto (mvnw) | Build e empacotamento com `mvn clean package`; atualmente o build usa `-DskipTests` porque não há suíte formal de testes |

---

## Frameworks e Bibliotecas Principais

| Camada | Framework / Biblioteca | Versão | Finalidade |
|---|---|---|---|
| Backend | Spring Boot | 3.2 | API e servidor principal da aplicação |
| Frontend | HTML + CSS + JavaScript vanilla | N/A | Interface web simples servida estaticamente pelo próprio backend |
| ORM / Acesso a dados | Spring Data JPA / Hibernate | Gerenciado pelo stack do Spring Boot 3.2 | Persistência e acesso aos dados do sistema |
| Testes | Não definido / inexistente no momento | N/A | O projeto ainda não possui suíte formal de testes automatizados |

---

## Banco de Dados

| Tipo | Tecnologia | Versão | Uso no sistema |
|---|---|---|---|
| Relacional | H2 (em memória) | Não especificada | Desenvolvimento local e execução simplificada em ambiente de dev |
| Relacional | PostgreSQL | Gerenciada pelo Railway | Banco de produção da aplicação hospedado no Railway |
| Cache | Não utilizado | N/A | O sistema não possui camada de cache dedicada no momento |
| Busca | Não utilizado | N/A | O sistema não possui mecanismo de busca dedicado |

---

## Infraestrutura e Cloud

| Item | Tecnologia | Observação |
|---|---|---|
| Cloud provider | Railway | PaaS onde rodam a aplicação Spring Boot e a instância de PostgreSQL no mesmo projeto |
| Containers | Não definido | Não existe Dockerfile no repositório; o deploy atual usa o mecanismo automático de build da plataforma |
| Orquestração | Não utilizada | Não há Kubernetes, ECS ou outra camada de orquestração definida |
| CI/CD | Deploy automático do Railway por push | Não existe pipeline formal de CI com testes, lint ou quality gates |
| Monitoramento | Não definido | Ainda não há ferramenta de observabilidade ou monitoramento configurada |

---

## Sistemas e Componentes Externos

> Registre todos os sistemas de terceiros, APIs externas e componentes compartilhados da organização que este sistema consome ou com os quais se integra.

| Sistema / Componente | Tipo | Finalidade | Como integra |
|---|---|---|---|
| Nenhum no momento | N/A | O fistSprint é standalone e não sincroniza dados com outras plataformas | N/A |
| Jira (explicitamente não integrado) | Referência de processo apenas | Pode ser usado em paralelo pela equipe, mas não há integração técnica nem troca de dados | Não integra |

---

## Ferramentas de Desenvolvimento

| Ferramenta | Finalidade |
|---|---|
| IntelliJ IDEA | IDE principal de desenvolvimento |
| Swagger / OpenAPI (`/swagger-ui.html`) | Documentação e teste manual da API |
| Google Fonts (Inter + JetBrains Mono) | Fontes consumidas no frontend estático |
