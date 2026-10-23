# Build e ferramentas

## Stack

- Java 21
- Spring Boot 3.5 (Web, Data JPA, Validation)
- Flyway para migrations (PostgreSQL)
- JUnit 5 e Mockito para testes unitários
- Testcontainers (PostgreSQL) para testes de integração
- JaCoCo com regra mínima de **80% de cobertura de linhas** no `verify`

## Maven Wrapper

O projeto usa o Maven Wrapper, então não é preciso instalar Maven.

```bash
./mvnw verify
```

No Windows, use `mvnw.cmd verify`.

Os testes de integração precisam de um Docker em execução, porque o Testcontainers sobe um PostgreSQL.

## Relatório de cobertura

Depois do `verify`, o relatório HTML fica em `target/site/jacoco/index.html`.
