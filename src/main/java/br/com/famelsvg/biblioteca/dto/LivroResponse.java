package br.com.famelsvg.biblioteca.dto;

import br.com.famelsvg.biblioteca.domain.Livro;

public record LivroResponse(Long id, String titulo, String autor, String isbn) {
    public static LivroResponse de(Livro l) {
        return new LivroResponse(l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn());
    }
}
