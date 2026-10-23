package br.com.famelsvg.biblioteca.domain;

import java.math.BigDecimal;

public final class RegrasEmprestimo {
    public static final int LIMITE_EMPRESTIMOS_ATIVOS = 3;
    public static final int PRAZO_DIAS = 14;
    public static final int PRAZO_RENOVACAO_DIAS = 14;
    public static final int MAX_RENOVACOES = 1;
    public static final BigDecimal MULTA_POR_DIA = new BigDecimal("2.00");

    private RegrasEmprestimo() {
    }
}
