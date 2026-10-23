package br.com.famelsvg.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CriarUsuarioRequest(@NotBlank String nome, @NotBlank @Email String email) {
}
