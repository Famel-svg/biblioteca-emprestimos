package br.com.famelsvg.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmprestimoRequest(@NotNull @Positive Long usuarioId, @NotNull @Positive Long exemplarId) {
}
