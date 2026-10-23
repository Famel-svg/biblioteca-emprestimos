# Camada de domínio

Mapa entre as regras de [regras-de-negocio.md](regras-de-negocio.md) e o código.

| Regra | Onde está |
|---|---|
| Constantes (3 empréstimos, 14 dias, multa R$ 2,00, 1 renovação) | `domain/RegrasEmprestimo.java` |
| RN1 exemplar disponível | `EmprestimoService.emprestar` |
| RN2 limite de empréstimos ativos | `EmprestimoService.emprestar` (via `EmprestimoRepository.countByUsuarioIdAndStatus`) |
| RN4 multa por dia de atraso | `EmprestimoService.devolver` |
| RN5 e RN6 renovação única e sem atraso | `EmprestimoService.renovar` |
| RN8 devolução fecha o empréstimo e libera o exemplar | `EmprestimoService.devolver` |

## Entidades

- `Livro`, `Exemplar`, `Usuario`, `Emprestimo` em `domain/`.
- Construtor protegido, sem Lombok. Associações são `LAZY`, e o status é gravado como `STRING`.
- O esquema vem da migration `db/migration/V1__schema.sql`, gerenciada pelo Flyway.

## Erros de negócio

- `NegocioException`: viola uma regra (HTTP 422 na API).
- `NaoEncontradoException`: id inexistente (HTTP 404 na API).
