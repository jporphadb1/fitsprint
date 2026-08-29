package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Criação/edição de conteúdo de uma bolina. Responsável e estado têm endpoints
 * próprios (atribuição pertence ao módulo de Ocupação, não ao Backlog).
 */
public record BolinaRequest(
        @NotBlank String titulo,
        @NotNull Integer tamanho,
        @NotNull Integer valor,
        @NotNull TaskType tipo) {
}
