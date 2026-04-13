# Project Portfolio API

API REST para gerenciamento de portfólio de projetos, permitindo controle de projetos, alocação de membros, acompanhamento de status e geração de relatórios.

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger (OpenAPI)
- JUnit / Mockito

## Funcionalidades

- Cadastro de projetos
- Atualização de projetos
- Controle de status com validação de transição
- Cálculo automático de risco do projeto
- Alocação e remoção de membros
- Limite de membros por projeto
- Limite de projetos ativos por membro
- Relatório consolidado de portfólio
- Filtros dinâmicos com paginação

## Regras de negócio

- Um projeto pode ter no máximo 10 membros
- Um membro pode participar de no máximo 3 projetos ativos
- Apenas membros com role EMPLOYEE podem ser alocados
- O projeto deve ter pelo menos 1 membro
- O status segue um fluxo definido, com exceção do CANCELADO que pode ocorrer a qualquer momento
- O risco do projeto é calculado com base em orçamento e duração

## Execução do projeto

### Pré-requisitos

- Java 21
- Maven
- Docker

### Subir banco de dados (PostgreSQL)

docker run -d -p 5432:5432 \
-e POSTGRES_DB=portfolio_db \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
postgres:16

### Rodar a aplicação

mvn clean install  
mvn spring-boot:run

A aplicação estará disponível em:

http://localhost:8080

## Autenticação

A aplicação utiliza Basic Auth.

Credenciais:

username: admin  
password: admin123

## Documentação da API

Swagger disponível em:

http://localhost:8080/swagger-ui.html

## Principais endpoints

### Projetos

- POST /api/projects
- GET /api/projects
- GET /api/projects/{id}
- PUT /api/projects/{id}
- PATCH /api/projects/{id}/status
- DELETE /api/projects/{id}

### Membros (API externa simulada)

- POST /external/members
- GET /external/members/{id}

### Alocação de membros

- POST /api/projects/{projectId}/members
- DELETE /api/projects/{projectId}/members/{memberId}
- GET /api/projects/{projectId}/members

### Relatório

- GET /api/reports/portfolio-summary

## Filtros disponíveis na listagem de projetos

Os seguintes parâmetros podem ser utilizados no endpoint `GET /api/projects`:

- status
- managerId
- startDateFrom
- startDateTo

Com suporte a paginação.

## Testes

mvn test

Os testes cobrem principalmente as regras de negócio na camada de service.

## Observações

- A API de membros foi implementada como serviço externo simulado em memória
- O cálculo de risco é feito dinamicamente
- O projeto segue arquitetura em camadas