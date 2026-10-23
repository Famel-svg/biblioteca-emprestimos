package br.com.famelsvg.biblioteca.dto;

import br.com.famelsvg.biblioteca.domain.Emprestimo;
import br.com.famelsvg.biblioteca.domain.StatusEmprestimo;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmprestimoResponse(Long id, Long exemplarId, Long usuarioId, LocalDate dataEmprestimo,
                                 LocalDate dataPrevista, LocalDate dataDevolucao, int renovacoes,
                                 BigDecimal multa, StatusEmprestimo status) {
    public static EmprestimoResponse de(Emprestimo e) {
        return new EmprestimoResponse(e.getId(), e.getExemplar().getId(), e.getUsuario().getId(),
                e.getDataEmprestimo(), e.getDataPrevista(), e.getDataDevolucao(), e.getRenovacoes(),
                e.getMulta(), e.getStatus());
    }
}
