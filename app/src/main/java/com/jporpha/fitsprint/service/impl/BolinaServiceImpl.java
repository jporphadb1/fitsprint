package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.BolinaRequest;
import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.entity.Bolina;
import com.jporpha.fitsprint.entity.Developer;
import com.jporpha.fitsprint.entity.Importancia;
import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.TaskStatus;
import com.jporpha.fitsprint.exception.BusinessRuleException;
import com.jporpha.fitsprint.exception.ResourceNotFoundException;
import com.jporpha.fitsprint.mapper.BolinaMapper;
import com.jporpha.fitsprint.repository.BolinaRepository;
import com.jporpha.fitsprint.repository.DeveloperRepository;
import com.jporpha.fitsprint.security.CurrentUser;
import com.jporpha.fitsprint.service.ActiveSprintResolver;
import com.jporpha.fitsprint.service.BacklogOrdenacao;
import com.jporpha.fitsprint.service.BolinaService;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class BolinaServiceImpl implements BolinaService {

    private static final Set<Integer> TAMANHOS_FIBONACCI = Set.of(1, 2, 3, 5, 8, 13, 21);

    private final BolinaRepository bolinaRepository;
    private final DeveloperRepository developerRepository;
    private final ActiveSprintResolver activeSprintResolver;
    private final BolinaMapper mapper;

    public BolinaServiceImpl(BolinaRepository bolinaRepository, DeveloperRepository developerRepository,
                              ActiveSprintResolver activeSprintResolver, BolinaMapper mapper) {
        this.bolinaRepository = bolinaRepository;
        this.developerRepository = developerRepository;
        this.activeSprintResolver = activeSprintResolver;
        this.mapper = mapper;
    }

    @Override
    public BolinaResponse criar(BolinaRequest request) {
        validarTamanho(request.tamanho());
        Sprint sprintAtivo = activeSprintResolver.resolve(CurrentUser.teamId());

        Bolina bolina = Bolina.builder()
                .titulo(request.titulo())
                .tamanho(request.tamanho())
                .valor(request.valor())
                .tipo(request.tipo())
                .estado(TaskStatus.TODO)
                .sprint(sprintAtivo)
                .teamId(sprintAtivo.getTeam().getId())
                .build();

        return mapper.toResponse(bolinaRepository.save(bolina));
    }

    @Override
    public List<BolinaResponse> listarPriorizado() {
        Sprint sprintAtivo = activeSprintResolver.resolve(CurrentUser.teamId());
        return bolinaRepository.findAllBySprintIdAndEliminadoFalse(sprintAtivo.getId()).stream()
                .sorted(BacklogOrdenacao.porPrioridade())
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BolinaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Override
    public BolinaResponse atualizar(Long id, BolinaRequest request) {
        validarTamanho(request.tamanho());
        Bolina bolina = buscarEntidade(id);
        bolina.setTitulo(request.titulo());
        bolina.setTamanho(request.tamanho());
        bolina.setValor(request.valor());
        bolina.setTipo(request.tipo());
        return mapper.toResponse(bolinaRepository.save(bolina));
    }

    @Override
    public BolinaResponse atualizarImportancia(Long id, Importancia importancia) {
        Bolina bolina = buscarEntidade(id);
        bolina.setImportancia(importancia);
        return mapper.toResponse(bolinaRepository.save(bolina));
    }

    @Override
    public BolinaResponse atualizarPrioridadeFinal(Long id, Integer prioridadeFinal) {
        Bolina bolina = buscarEntidade(id);
        bolina.setPrioridadeFinal(prioridadeFinal);
        return mapper.toResponse(bolinaRepository.save(bolina));
    }

    @Override
    public BolinaResponse atualizarEstado(Long id, TaskStatus estado) {
        Bolina bolina = buscarEntidade(id);
        bolina.setEstado(estado);
        return mapper.toResponse(bolinaRepository.save(bolina));
    }

    @Override
    public BolinaResponse atualizarFuera(Long id, boolean fuera) {
        Bolina bolina = buscarEntidade(id);
        bolina.setFuera(fuera);
        return mapper.toResponse(bolinaRepository.save(bolina));
    }

    @Override
    public BolinaResponse atualizarResponsavel(Long id, Long developerId) {
        Bolina bolina = buscarEntidade(id);

        if (developerId == null) {
            bolina.setDeveloper(null);
        } else {
            Developer developer = developerRepository.findById(developerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Developer não encontrado: " + developerId));
            if (!developer.getTeam().getId().equals(bolina.getTeamId())) {
                throw new BusinessRuleException("Developer não pertence ao mesmo time da bolina");
            }
            bolina.setDeveloper(developer);
        }

        return mapper.toResponse(bolinaRepository.save(bolina));
    }

    @Override
    public void remover(Long id) {
        Bolina bolina = buscarEntidade(id);
        bolina.setEliminado(true);
        bolinaRepository.save(bolina);
    }

    private Bolina buscarEntidade(Long id) {
        return bolinaRepository.findByIdAndTeamId(id, CurrentUser.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Bolina não encontrada: " + id));
    }

    private void validarTamanho(Integer tamanho) {
        if (!TAMANHOS_FIBONACCI.contains(tamanho)) {
            throw new BusinessRuleException("Tamanho deve seguir a escala Fibonacci: " + TAMANHOS_FIBONACCI);
        }
    }
}
