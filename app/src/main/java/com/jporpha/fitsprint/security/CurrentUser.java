package com.jporpha.fitsprint.security;

import com.jporpha.fitsprint.entity.Role;
import com.jporpha.fitsprint.exception.BusinessRuleException;
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

    public static boolean isSuperAdmin() {
        return get().role() == Role.SUPER_ADMIN;
    }

    /** team_id do usuário autenticado; usado para filtrar toda leitura/escrita por multitenancy. */
    public static Long teamId() {
        Long teamId = get().teamId();
        if (teamId == null) {
            throw new IllegalStateException("Usuário autenticado não pertence a um time");
        }
        return teamId;
    }

    /**
     * Resolve qual team_id vale para uma operação acotada a equipe (Sprint, Developer, vistas).
     * SUPER_ADMIN não tem team próprio (Sofia_context.md: "é o único papel não limitado por
     * team_id") e por isso deve informar explicitamente qual equipe está operando. ADMIN/USER só
     * podem operar no próprio time — informar um teamId diferente do seu é rejeitado.
     */
    public static Long resolveTeamId(Long requestedTeamId) {
        if (isSuperAdmin()) {
            if (requestedTeamId == null) {
                throw new BusinessRuleException("SUPER_ADMIN deve informar teamId explicitamente para esta operação");
            }
            return requestedTeamId;
        }

        Long ownTeamId = teamId();
        if (requestedTeamId != null && !requestedTeamId.equals(ownTeamId)) {
            throw new BusinessRuleException("Não é permitido operar em um time diferente do seu");
        }
        return ownTeamId;
    }
}
