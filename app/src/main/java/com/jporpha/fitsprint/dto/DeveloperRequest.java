package com.jporpha.fitsprint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** {@code teamId} obrigatório apenas para SUPER_ADMIN (ADMIN sempre opera no próprio time). */
public record DeveloperRequest(@NotBlank String nome, @NotNull Integer capacidadeTotal, Long teamId) {
}
