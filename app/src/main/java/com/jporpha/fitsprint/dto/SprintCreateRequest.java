package com.jporpha.fitsprint.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * {@code bufferPercentual} nulo usa o default de 20%. {@code teamId} obrigatório apenas
 * para SUPER_ADMIN. O sprint é criado como CLOSED — precisa de um {@code ativar} explícito
 * para virar o sprint corrente do time (regra de "só um ACTIVE por vez" fica só no ativar).
 */
public record SprintCreateRequest(@NotBlank String nome, Integer bufferPercentual, Long teamId) {
}
