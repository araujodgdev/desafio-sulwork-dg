# Sulwork Cafe

Aplicação web para organizar os cafés da manhã da equipe, evitando conflitos de participantes, CPF e itens repetidos no mesmo dia.

O projeto foi construído com Angular no frontend, Spring Boot no backend, PostgreSQL como banco de dados e Docker Compose para executar tudo localmente.

## Funcionalidades

- Cadastro, edição, listagem e exclusão de cafés da manhã.
- Cadastro e exclusão de participantes por café.
- Cadastro, edição e exclusão dos itens que cada participante vai levar.
- Validação de CPF com 11 dígitos e CPF válido.
- Bloqueio de participante repetido no mesmo café.
- Bloqueio de item repetido no mesmo café, mesmo entre participantes diferentes.
- Data do café precisa ser maior que a data atual no cadastro.
- Marcação de item como `TROUXE` ou `NAO_TROUXE` no dia do café.
- Itens pendentes de cafés passados são marcados automaticamente como `NAO_TROUXE` ao consultar a API.
- Mensagens de erro padronizadas via `@RestControllerAdvice`.

## Stack

- Frontend: Angular 21
- Backend: Java 25, Spring Boot 4, Spring WebMVC, Spring Validation, JPA com Native Query
- Banco de dados: PostgreSQL 16
- Testes backend: JUnit 5, MockMvc, H2
- Testes frontend: Vitest e Cypress
- Documentação da API: Swagger/OpenAPI
- Containers: Docker e Docker Compose

## Estrutura

```text
.
├── docker-compose.yml
├── frontend/       # Angular
└── sulwork-cafe/   # Spring Boot API
```

## Rodando com Docker

Pré-requisitos:

- Docker
- Docker Compose

Suba frontend, backend e banco de dados:

```bash
docker compose up --build
```

Acesse:

- Frontend: http://localhost:4200
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- PostgreSQL: `localhost:5432`

Credenciais do banco no Docker:

```text
database: sulwork_cafe
user: sulwork
password: sulwork
```

Para parar os containers:

```bash
docker compose down
```

Para parar e remover também os dados do banco:

```bash
docker compose down -v
```

> [!NOTE]
> Se as portas `4200`, `8080` ou `5432` já estiverem em uso, pare os serviços locais que estiverem usando essas portas antes de subir o Compose.

## Rodando localmente sem Docker completo

Este modo é útil durante o desenvolvimento no Eclipse/STS/VS Code.

Pré-requisitos:

- Java 25
- Node.js compatível com Angular 21
- npm
- Docker, apenas para subir o PostgreSQL

Suba somente o banco:

```bash
docker compose up -d db
```

Inicie o backend:

```bash
cd sulwork-cafe
./mvnw spring-boot:run
```

Inicie o frontend em outro terminal:

```bash
cd frontend
npm ci
npm start
```

Acesse o frontend em:

```text
http://localhost:4200
```

## Configuração do backend

O backend local usa as configurações de `sulwork-cafe/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sulwork_cafe
spring.datasource.username=sulwork
spring.datasource.password=sulwork
spring.sql.init.mode=always
```

No Docker Compose, essas configurações são sobrescritas por variáveis de ambiente para que a API acesse o banco pelo hostname `db`.

## Principais endpoints

### Cafés da manhã

- `GET /breakfasts`
- `GET /breakfasts/{id}`
- `POST /breakfasts`
- `PUT /breakfasts/{id}`
- `DELETE /breakfasts/{id}`

Payload para criar ou atualizar café:

```json
{
  "breakfastDate": "2026-06-10",
  "breakfastTime": "08:30",
  "location": "Sala de reunião"
}
```

### Participações

- `POST /participations`
- `DELETE /participations/{participationId}`

Payload para cadastrar participação:

```json
{
  "breakfastId": 1,
  "name": "João Silva",
  "cpf": "73244216013"
}
```

### Itens

- `POST /participations/{participationId}/items`
- `PUT /items/{itemId}`
- `DELETE /items/{itemId}`
- `PATCH /items/{itemId}/status`

Payload para cadastrar ou atualizar item:

```json
{
  "name": "Suco de acerola"
}
```

Payload para atualizar status:

```json
{
  "status": "TROUXE"
}
```

Status aceitos:

- `PENDENTE`
- `TROUXE`
- `NAO_TROUXE`

## Testes

Backend:

```bash
cd sulwork-cafe
./mvnw test
```

Frontend unitário:

```bash
cd frontend
npm test -- --watch=false
```

Frontend e2e com Cypress:

```bash
cd frontend
npm run e2e
```

Antes de rodar o Cypress, mantenha o frontend ativo em `http://localhost:4200`. Os testes e2e usam interceptações HTTP para validar o fluxo da interface sem depender do backend ou do banco rodando.

Para abrir o Cypress em modo interativo:

```bash
cd frontend
npm run e2e:open
```

## Build local

Backend:

```bash
cd sulwork-cafe
./mvnw clean package
```

Frontend:

```bash
cd frontend
npm run build
```

Docker:

```bash
docker compose build
```

## Observações para avaliação

- As operações de banco no backend usam SQL nativo via `EntityManager`.
- A API usa DTOs para entrada e saída de dados.
- As regras de negócio ficam nas classes de service.
- Os erros de validação e exceções comuns retornam JSON padronizado.
- O Swagger está disponível em `/swagger-ui.html` quando o backend está rodando.
