package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import com.jporpha.fitsprint.entity.Bolina;
import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.TaskStatus;
import com.jporpha.fitsprint.entity.ZonaOperativa;
import com.jporpha.fitsprint.mapper.BolinaMapper;
import com.jporpha.fitsprint.repository.BolinaRepository;
import com.jporpha.fitsprint.security.CurrentUser;
import com.jporpha.fitsprint.service.ActiveSprintResolver;
import com.jporpha.fitsprint.service.BacklogOrdenacao;
import com.jporpha.fitsprint.service.FiltrosService;
import com.jporpha.fitsprint.service.OcupacaoService;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class FiltrosServiceImpl implements FiltrosService {

    private final BolinaRepository bolinaRepository;
    private final ActiveSprintResolver activeSprintResolver;
    private final BolinaMapper mapper;
    private final OcupacaoService ocupacaoService;

    public FiltrosServiceImpl(BolinaRepository bolinaRepository, ActiveSprintResolver activeSprintResolver,
                              BolinaMapper mapper, OcupacaoService ocupacaoService) {
        this.bolinaRepository = bolinaRepository;
        this.activeSprintResolver = activeSprintResolver;
        this.mapper = mapper;
        this.ocupacaoService = ocupacaoService;
    }

    @Override
    public List<BolinaResponse> vistaPrincipal(Long teamId, Long developerId, Boolean semResponsavel,
                                                TaskStatus estado, ZonaOperativa zona) {
        Predicate<Bolina> filtro = b -> true;

        if (Boolean.TRUE.equals(semResponsavel)) {
            filtro = filtro.and(b -> b.getDeveloper() == null);
        } else if (developerId != null) {
            filtro = filtro.and(b -> b.getDeveloper() != null && developerId.equals(b.getDeveloper().getId()));
        }
        if (estado != null) {
            filtro = filtro.and(b -> b.getEstado() == estado);
        }
        if (zona != null) {
            filtro = filtro.and(b -> b.getZona() == zona);
        }

        return bolinasDoSprintAtivo(teamId).filter(filtro)
                .sorted(BacklogOrdenacao.porPrioridade())
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<BolinaResponse> vistaUrgencias(Long teamId) {
        return bolinasDoSprintAtivo(teamId)
                .filter(b -> b.getZona() == ZonaOperativa.CONTINUIDAD_OPERATIVA)
                .sorted(BacklogOrdenacao.porPrioridade())
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DisponibilidadeDeveloperResponse> vistaDisponibilidade(Long teamId) {
        return ocupacaoService.disponibilidadePorDeveloper(teamId);
    }

    private Stream<Bolina> bolinasDoSprintAtivo(Long teamId) {
        Sprint sprintAtivo = activeSprintResolver.resolve(CurrentUser.resolveTeamId(teamId));
        return bolinaRepository.findAllBySprintIdAndEliminadoFalse(sprintAtivo.getId()).stream();
    }
}
