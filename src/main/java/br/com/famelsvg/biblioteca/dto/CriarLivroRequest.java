package br.com.famelsvg.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarLivroRequest(@NotBlank String titulo, @NotBlank String autor, @NotBlank String isbn) {
}
