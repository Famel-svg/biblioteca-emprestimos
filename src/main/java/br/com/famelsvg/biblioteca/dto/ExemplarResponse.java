package br.com.famelsvg.biblioteca.dto;

import br.com.famelsvg.biblioteca.domain.Exemplar;
import br.com.famelsvg.biblioteca.domain.StatusExemplar;

public record ExemplarResponse(Long id, Long livroId, StatusExemplar status) {
    public static ExemplarResponse de(Exemplar e) {
        return new ExemplarResponse(e.getId(), e.getLivro().getId(), e.getStatus());
    }
}
