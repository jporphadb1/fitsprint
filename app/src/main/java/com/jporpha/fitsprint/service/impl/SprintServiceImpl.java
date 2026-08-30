package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.SprintCreateRequest;
import com.jporpha.fitsprint.dto.SprintResponse;
import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.SprintStatus;
import com.jporpha.fitsprint.entity.Team;
import com.jporpha.fitsprint.exception.ResourceNotFoundException;
import com.jporpha.fitsprint.repository.SprintRepository;
import com.jporpha.fitsprint.repository.TeamRepository;
import com.jporpha.fitsprint.security.CurrentUser;
import com.jporpha.fitsprint.service.SprintService;
import org.springframework.stereotype.Service;

@Service
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepository;
    private final TeamRepository teamRepository;

    public SprintServiceImpl(SprintRepository sprintRepository, TeamRepository teamRepository) {
        this.sprintRepository = sprintRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public SprintResponse criar(SprintCreateRequest request) {
        Long teamId = CurrentUser.resolveTeamId(request.teamId());
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado: " + teamId));

        Sprint sprint = Sprint.builder()
                .nome(request.nome())
                .team(team)
                .status(SprintStatus.CLOSED)
                .bufferPercentual(request.bufferPercentual() == null ? 20 : request.bufferPercentual())
                .build();

        return toResponse(sprintRepository.save(sprint));
    }

    @Override
    public SprintResponse ativar(Long id) {
        Sprint sprint = buscarEntidade(id);
        Long teamId = sprint.getTeam().getId();

        sprintRepository.findByTeamIdAndStatus(teamId, SprintStatus.ACTIVE)
                .filter(atual -> !atual.getId().equals(sprint.getId()))
                .ifPresent(atual -> {
                    atual.setStatus(SprintStatus.CLOSED);
                    sprintRepository.save(atual);
                });

        sprint.setStatus(SprintStatus.ACTIVE);
        return toResponse(sprintRepository.save(sprint));
    }

    @Override
    public SprintResponse fechar(Long id) {
        Sprint sprint = buscarEntidade(id);
        sprint.setStatus(SprintStatus.CLOSED);
        return toResponse(sprintRepository.save(sprint));
    }

    private Sprint buscarEntidade(Long id) {
        if (CurrentUser.isSuperAdmin()) {
            return sprintRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint não encontrado: " + id));
        }
        return sprintRepository.findByIdAndTeamId(id, CurrentUser.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint não encontrado: " + id));
    }

    private SprintResponse toResponse(Sprint sprint) {
        return new SprintResponse(sprint.getId(), sprint.getNome(), sprint.getStatus(),
                sprint.getBufferPercentual(), sprint.getTeam().getId(), sprint.getDataCriacao());
    }
}
