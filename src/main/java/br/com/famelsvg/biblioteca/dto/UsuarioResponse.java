package br.com.famelsvg.biblioteca.dto;

import br.com.famelsvg.biblioteca.domain.Usuario;

public record UsuarioResponse(Long id, String nome, String email) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail());
    }
}
