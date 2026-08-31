package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * {@code password} obrigatório na criação; na atualização, nulo/blank mantém a senha atual.
 * {@code teamId} nulo apenas para SUPER_ADMIN.
 */
public record UserRequest(
        @NotBlank @Email String email,
        String password,
        @NotNull Role role,
        Long teamId) {
}
