package com.jporpha.fitsprint.security;

import com.jporpha.fitsprint.entity.Role;

/**
 * Identidade extraída do JWT em cada requisição. {@code teamId} é nulo apenas
 * para SUPER_ADMIN (gestão de plataforma, sem time associado).
 */
public record AuthenticatedUser(Long userId, String email, Role role, Long teamId) {
}
