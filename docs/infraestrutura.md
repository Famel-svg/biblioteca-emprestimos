# Infraestrutura: Docker e CI

## Dockerfile (multi-stage)

1. **build**: `eclipse-temurin:21-jdk` roda `./mvnw package`. As dependências são baixadas antes do código, para o cache do Docker funcionar.
2. **runtime**: `eclipse-temurin:21-jre` leva só o jar e roda com um usuário sem privilégios de root.

```bash
docker build -t biblioteca-emprestimos .
```

## docker-compose.yml

Sobe o PostgreSQL 16 e a aplicação. A aplicação só inicia quando o banco responde ao `pg_isready`.

```bash
docker compose up --build
```

- API em `http://localhost:8080`
- PostgreSQL em `localhost:5432` (banco `biblioteca`, usuário `biblioteca`)

As credenciais são de desenvolvimento e ficam no próprio compose. Não use em produção.

## GitHub Actions

O workflow `.github/workflows/ci.yml` roda em todo push na `main` e em todo pull request:

1. `./mvnw verify`: testes unitários, testes de integração com Testcontainers e a checagem do JaCoCo (80% de linhas).
2. `docker build`: garante que a imagem monta.

O badge do status fica no README.
