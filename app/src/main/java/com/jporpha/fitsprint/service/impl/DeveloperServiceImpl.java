package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.DeveloperRequest;
import com.jporpha.fitsprint.dto.DeveloperResponse;
import com.jporpha.fitsprint.entity.Developer;
import com.jporpha.fitsprint.entity.Team;
import com.jporpha.fitsprint.exception.ResourceNotFoundException;
import com.jporpha.fitsprint.repository.DeveloperRepository;
import com.jporpha.fitsprint.repository.TeamRepository;
import com.jporpha.fitsprint.security.CurrentUser;
import com.jporpha.fitsprint.service.DeveloperService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DeveloperServiceImpl implements DeveloperService {

    private final DeveloperRepository developerRepository;
    private final TeamRepository teamRepository;

    public DeveloperServiceImpl(DeveloperRepository developerRepository, TeamRepository teamRepository) {
        this.developerRepository = developerRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public DeveloperResponse criar(DeveloperRequest request) {
        Long teamId = CurrentUser.resolveTeamId(request.teamId());
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado: " + teamId));

        Developer developer = Developer.builder()
                .nome(request.nome())
                .capacidadeTotal(request.capacidadeTotal())
                .team(team)
                .build();

        return toResponse(developerRepository.save(developer));
    }

    @Override
    public List<DeveloperResponse> listar(Long teamId) {
        Long resolvido = CurrentUser.resolveTeamId(teamId);
        return developerRepository.findAllByTeamId(resolvido).stream().map(this::toResponse).toList();
    }

    @Override
    public DeveloperResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Override
    public DeveloperResponse atualizar(Long id, DeveloperRequest request) {
        Developer developer = buscarEntidade(id);
        developer.setNome(request.nome());
        developer.setCapacidadeTotal(request.capacidadeTotal());
        return toResponse(developerRepository.save(developer));
    }

    @Override
    public void remover(Long id) {
        developerRepository.delete(buscarEntidade(id));
    }

    private Developer buscarEntidade(Long id) {
        if (CurrentUser.isSuperAdmin()) {
            return developerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Developer não encontrado: " + id));
        }
        return developerRepository.findByIdAndTeamId(id, CurrentUser.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Developer não encontrado: " + id));
    }

    private DeveloperResponse toResponse(Developer developer) {
        return new DeveloperResponse(developer.getId(), developer.getNome(), developer.getCapacidadeTotal(),
                developer.getTeam().getId());
    }
}
