package com.jporpha.fitsprint.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Acesso ao usuário autenticado corrente, extraído do JWT pelo {@link JwtAuthenticationFilter}.
 */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static AuthenticatedUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("Nenhum usuário autenticado no contexto de segurança");
        }
        return user;
    }

    /** team_id do usuário autenticado; usado para filtrar toda leitura/escrita por multitenancy. */
    public static Long teamId() {
        Long teamId = get().teamId();
        if (teamId == null) {
            throw new IllegalStateException("Usuário autenticado não pertence a um time");
        }
        return teamId;
    }
}
