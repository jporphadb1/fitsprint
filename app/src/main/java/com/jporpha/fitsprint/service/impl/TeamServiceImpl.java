package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.TeamRequest;
import com.jporpha.fitsprint.dto.TeamResponse;
import com.jporpha.fitsprint.entity.Team;
import com.jporpha.fitsprint.exception.ResourceNotFoundException;
import com.jporpha.fitsprint.repository.TeamRepository;
import com.jporpha.fitsprint.service.TeamService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public TeamResponse criar(TeamRequest request) {
        Team team = teamRepository.save(Team.builder().nome(request.nome()).build());
        return toResponse(team);
    }

    @Override
    public List<TeamResponse> listar() {
        return teamRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public TeamResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Override
    public TeamResponse atualizar(Long id, TeamRequest request) {
        Team team = buscarEntidade(id);
        team.setNome(request.nome());
        return toResponse(teamRepository.save(team));
    }

    @Override
    public void remover(Long id) {
        teamRepository.delete(buscarEntidade(id));
    }

    private Team buscarEntidade(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado: " + id));
    }

    private TeamResponse toResponse(Team team) {
        return new TeamResponse(team.getId(), team.getNome());
    }
}
