package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.SprintStatus;
import com.jporpha.fitsprint.exception.ResourceNotFoundException;
import com.jporpha.fitsprint.repository.SprintRepository;
import org.springframework.stereotype.Component;

/**
 * Resolve o sprint ACTIVE do time corrente. Só pode existir um por time
 * (FitSprint_Business_Rules_RAG.md) — usado por todos os módulos que operam
 * sobre o sprint ativo (Backlog, Buffer, Capacidade, Ocupação, Filtros).
 */
@Component
public class ActiveSprintResolver {

    private final SprintRepository sprintRepository;

    public ActiveSprintResolver(SprintRepository sprintRepository) {
        this.sprintRepository = sprintRepository;
    }

    public Sprint resolve(Long teamId) {
        return sprintRepository.findByTeamIdAndStatus(teamId, SprintStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Não há sprint ativo para este time"));
    }
}
