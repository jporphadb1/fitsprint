package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Criação/edição de conteúdo de uma bolina. Responsável e estado têm endpoints
 * próprios (atribuição pertence ao módulo de Ocupação, não ao Backlog).
 *
 * <p>{@code teamId} só é considerado na criação (define em qual time/sprint ativo a bolina
 * nasce); obrigatório apenas para SUPER_ADMIN, que não tem time próprio. Na edição é ignorado
 * — o time de uma bolina já existente nunca muda.
 */
public record BolinaRequest(
        @NotBlank String titulo,
        @NotNull Integer tamanho,
        @NotNull Integer valor,
        @NotNull TaskType tipo,
        Long teamId) {
}
