## Learned User Preferences

- Communicates in Portuguese for Java learning questions, code review, and fix requests.
- When asking to fix code (e.g. "corrija"), expects the agent to apply changes and verify compile (`./mvnw compile`), not only explain the fix.
- Values concise explanations of Java 8+ concepts (streams, method references, `Optional`) tied to code in this repository.

## Learned Workspace Facts

- Monorepo layout: `sulwork-cafe` (Spring Boot API) and `frontend/` (Angular UI with its own `frontend/AGENTS.md` for Angular conventions).
- Backend persistence uses native SQL via `EntityManager` in `@Repository` classes, mapping `Object[]` rows to record DTOs—not JPA entity repositories for the main flows.
- PostgreSQL runs at `localhost:5432` per `sulwork-cafe/src/main/resources/application.properties`.
- OpenAPI UI (springdoc): `http://localhost:8080/swagger-ui/index.html`; spec at `/v3/api-docs` (default port 8080).
- Build/run backend from `sulwork-cafe` with `./mvnw` (e.g. `./mvnw compile`, `./mvnw spring-boot:run`).
- Schema chain: `breakfasts` → `participations` → `breakfast_items`; there is no separate `items` table.
- Nested responses (`BreakfastDTO.participations`, `ParticipationDTO.items`) are built with separate queries and composition in service/repository layers—not nested lists inside one SQL `Object[]` row index.
- Native queries should use explicit column lists; map Postgres numeric IDs with `((Number) row[n]).longValue()`.
