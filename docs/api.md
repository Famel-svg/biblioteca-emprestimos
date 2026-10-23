# API REST

Base: `http://localhost:8080`. Corpos e respostas em JSON. Erros usam `ProblemDetail` (RFC 9457).

## Endpoints

| Método | Caminho | Resposta | Descrição |
|---|---|---|---|
| POST | `/livros` | 201 | Cadastra um livro (título, autor, ISBN). |
| GET | `/livros/{id}` | 200 / 404 | Busca um livro. |
| POST | `/livros/{id}/exemplares` | 201 | Cria um exemplar `DISPONIVEL` para o livro. |
| POST | `/usuarios` | 201 | Cadastra um usuário (nome, e-mail). |
| GET | `/usuarios/{id}` | 200 / 404 | Busca um usuário. |
| POST | `/emprestimos` | 201 / 404 / 422 | Empresta um exemplar a um usuário. |
| POST | `/emprestimos/{id}/devolucao` | 200 / 404 / 422 | Devolve e calcula a multa. |
| POST | `/emprestimos/{id}/renovacao` | 200 / 404 / 422 | Renova uma vez, sem atraso. |

## Códigos de erro

- **400**: corpo inválido (validação do Bean Validation).
- **404**: id inexistente.
- **409**: ISBN ou e-mail duplicado.
- **422**: viola uma regra de negócio (exemplar indisponível, limite de 3, empréstimo atrasado, renovação já usada).

## Exemplo

```bash
curl -X POST http://localhost:8080/usuarios \
  -H 'Content-Type: application/json' \
  -d '{"nome": "Ana Exemplo", "email": "ana@exemplo.com"}'
```

## Testes de integração

`EmprestimoApiTest` sobe a aplicação com Testcontainers (PostgreSQL 16) e cobre o fluxo completo: cadastro, empréstimo, bloqueio de exemplar indisponível, devolução, renovação, limite de 3 e os erros 400, 404 e 409.

## Nota sobre Docker 29

O Testcontainers 1.21 negocia uma versão antiga da API Docker, que o Docker Engine 29 recusa. `src/test/resources/docker-java.properties` fixa `api.version=1.44`. Pode ser removido quando o Testcontainers for atualizado.
