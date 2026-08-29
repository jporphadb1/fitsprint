package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import com.jporpha.fitsprint.entity.Developer;
import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.StatusOcupacao;
import com.jporpha.fitsprint.mapper.BolinaMapper;
import com.jporpha.fitsprint.repository.BolinaRepository;
import com.jporpha.fitsprint.repository.DeveloperRepository;
import com.jporpha.fitsprint.security.CurrentUser;
import com.jporpha.fitsprint.service.ActiveSprintResolver;
import com.jporpha.fitsprint.service.OcupacaoCalculator;
import com.jporpha.fitsprint.service.OcupacaoCalculator.Ocupacao;
import com.jporpha.fitsprint.service.OcupacaoService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class OcupacaoServiceImpl implements OcupacaoService {

    private final BolinaRepository bolinaRepository;
    private final DeveloperRepository developerRepository;
    private final ActiveSprintResolver activeSprintResolver;
    private final OcupacaoCalculator ocupacaoCalculator;
    private final BolinaMapper bolinaMapper;

    public OcupacaoServiceImpl(BolinaRepository bolinaRepository, DeveloperRepository developerRepository,
                               ActiveSprintResolver activeSprintResolver, OcupacaoCalculator ocupacaoCalculator,
                               BolinaMapper bolinaMapper) {
        this.bolinaRepository = bolinaRepository;
        this.developerRepository = developerRepository;
        this.activeSprintResolver = activeSprintResolver;
        this.ocupacaoCalculator = ocupacaoCalculator;
        this.bolinaMapper = bolinaMapper;
    }

    @Override
    public List<BolinaResponse> mapaAlocacao() {
        Sprint sprintAtivo = activeSprintResolver.resolve(CurrentUser.teamId());
        return bolinaRepository.findAllBySprintIdAndEliminadoFalse(sprintAtivo.getId()).stream()
                .map(bolinaMapper::toResponse)
                .toList();
    }

    @Override
    public List<DisponibilidadeDeveloperResponse> disponibilidadePorDeveloper() {
        Long teamId = CurrentUser.teamId();
        Sprint sprintAtivo = activeSprintResolver.resolve(teamId);

        List<Developer> developers = developerRepository.findAllByTeamId(teamId);
        Map<Long, Ocupacao> ocupacaoPorDeveloper = ocupacaoCalculator.calcularOcupacaoPorDeveloper(sprintAtivo.getId());

        return developers.stream()
                .map(developer -> montarDisponibilidade(developer, ocupacaoPorDeveloper))
                .toList();
    }

    private DisponibilidadeDeveloperResponse montarDisponibilidade(Developer developer, Map<Long, Ocupacao> ocupacoes) {
        Ocupacao ocupacao = ocupacoes.getOrDefault(developer.getId(), OcupacaoCalculator.VAZIA);
        int capacidadeTotal = developer.getCapacidadeTotal() == null ? 0 : developer.getCapacidadeTotal();

        StatusOcupacao status;
        if (ocupacao.quantidadeTarefas() == 0) {
            status = StatusOcupacao.SEM_TAREFAS;
        } else if (ocupacao.storyPoints() > capacidadeTotal) {
            status = StatusOcupacao.SOBRECARREGADO;
        } else {
            status = StatusOcupacao.DENTRO_DA_CAPACIDADE;
        }

        return new DisponibilidadeDeveloperResponse(developer.getId(), developer.getNome(), capacidadeTotal,
                ocupacao.storyPoints(), capacidadeTotal - ocupacao.storyPoints(), ocupacao.quantidadeTarefas(), status);
    }
}
