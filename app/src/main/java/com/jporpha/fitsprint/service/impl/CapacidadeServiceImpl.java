package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.CapacidadeConsolidadaResponse;
import com.jporpha.fitsprint.dto.DeveloperCapacidadeResponse;
import com.jporpha.fitsprint.entity.Developer;
import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.repository.DeveloperRepository;
import com.jporpha.fitsprint.security.CurrentUser;
import com.jporpha.fitsprint.service.ActiveSprintResolver;
import com.jporpha.fitsprint.service.CapacidadeService;
import com.jporpha.fitsprint.service.OcupacaoCalculator;
import com.jporpha.fitsprint.service.OcupacaoCalculator.Ocupacao;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CapacidadeServiceImpl implements CapacidadeService {

    private final DeveloperRepository developerRepository;
    private final ActiveSprintResolver activeSprintResolver;
    private final OcupacaoCalculator ocupacaoCalculator;

    public CapacidadeServiceImpl(DeveloperRepository developerRepository, ActiveSprintResolver activeSprintResolver,
                                  OcupacaoCalculator ocupacaoCalculator) {
        this.developerRepository = developerRepository;
        this.activeSprintResolver = activeSprintResolver;
        this.ocupacaoCalculator = ocupacaoCalculator;
    }

    @Override
    public CapacidadeConsolidadaResponse consultar(Long teamIdParam) {
        Long teamId = CurrentUser.resolveTeamId(teamIdParam);
        Sprint sprintAtivo = activeSprintResolver.resolve(teamId);

        List<Developer> developers = developerRepository.findAllByTeamId(teamId);
        Map<Long, Ocupacao> ocupacaoPorDeveloper = ocupacaoCalculator.calcularOcupacaoPorDeveloper(sprintAtivo.getId());

        List<DeveloperCapacidadeResponse> porDeveloper = developers.stream()
                .map(developer -> {
                    int usada = ocupacaoPorDeveloper.getOrDefault(developer.getId(), OcupacaoCalculator.VAZIA).storyPoints();
                    int total = developer.getCapacidadeTotal() == null ? 0 : developer.getCapacidadeTotal();
                    return new DeveloperCapacidadeResponse(developer.getId(), developer.getNome(), total, usada, total - usada);
                })
                .toList();

        int capacidadeTotalTime = porDeveloper.stream().mapToInt(DeveloperCapacidadeResponse::capacidadeTotal).sum();
        int capacidadeUsadaTime = porDeveloper.stream().mapToInt(DeveloperCapacidadeResponse::capacidadeUsada).sum();

        return new CapacidadeConsolidadaResponse(capacidadeTotalTime, capacidadeUsadaTime,
                capacidadeTotalTime - capacidadeUsadaTime, porDeveloper);
    }
}
