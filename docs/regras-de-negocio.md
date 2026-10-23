# Regras de negócio

Documento de referência do domínio. Onde o enunciado era ambíguo, a decisão tomada está marcada como **Decisão**.

## Entidades

| Entidade | Descrição |
|---|---|
| `Livro` | Título de obra (título, autor, ISBN). Pode ter vários exemplares. |
| `Exemplar` | Cópia física de um `Livro`. Tem status `DISPONIVEL` ou `EMPRESTADO`. |
| `Usuario` | Pessoa que pega livros emprestados (nome, e-mail). |
| `Emprestimo` | Vínculo entre um `Exemplar` e um `Usuario`, com datas e status. |

## Regras

| # | Regra | Valor |
|---|---|---|
| RN1 | Um exemplar só pode ser emprestado se estiver `DISPONIVEL`. | — |
| RN2 | Um usuário pode ter no máximo **3 empréstimos ativos** ao mesmo tempo. | `3` |
| RN3 | Prazo padrão de devolução. | **14 dias corridos** a partir do empréstimo (Decisão) |
| RN4 | Multa por atraso: **R$ 2,00 por dia** de atraso, por exemplar. Valor fictício. | `2.00` |
| RN5 | Renovação: no máximo **uma vez** por empréstimo. | `1` |
| RN6 | Só é possível renovar se o empréstimo **não estiver atrasado**. | — |
| RN7 | Renovar soma **14 dias** à data prevista atual (Decisão). | `14` |
| RN8 | Devolução fecha o empréstimo, calcula a multa e torna o exemplar `DISPONIVEL`. | — |

## Decisões tomadas sobre pontos ambíguos

- **Prazo e renovação:** o enunciado não fixava prazos. Adotado 14 dias para o empréstimo e 14 dias para a renovação.
- **Atraso:** um empréstimo está atrasado quando a data atual é posterior à data prevista. Dias de atraso = dias corridos entre a data prevista e a data de devolução (zero se devolvido no prazo).
- **Multa:** calculada na devolução, `dias de atraso × R$ 2,00`. Não há bloqueio de novos empréstimos por multa pendente (fora do escopo do enunciado).
- **Reserva:** não existe. Exemplar indisponível simplesmente recusa o empréstimo.
- **Relógio:** todas as regras de data usam um `Clock` injetado, para os testes serem determinísticos.

## Dados de exemplo

Todos os dados usados em exemplos, seeds e testes são fictícios. Nenhum dado pessoal real.
