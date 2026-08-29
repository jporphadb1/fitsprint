package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.BufferResponse;
import com.jporpha.fitsprint.entity.Sprint;
import com.jporpha.fitsprint.entity.ZonaOperativa;
import com.jporpha.fitsprint.entity.SemaforoBuffer;
import com.jporpha.fitsprint.repository.BolinaRepository;
import com.jporpha.fitsprint.repository.DeveloperRepository;
import com.jporpha.fitsprint.security.CurrentUser;
import com.jporpha.fitsprint.service.ActiveSprintResolver;
import com.jporpha.fitsprint.service.BufferService;
import org.springframework.stereotype.Service;

@Service
public class BufferServiceImpl implements BufferService {

    private final ActiveSprintResolver activeSprintResolver;
    private final DeveloperRepository developerRepository;
    private final BolinaRepository bolinaRepository;

    public BufferServiceImpl(ActiveSprintResolver activeSprintResolver, DeveloperRepository developerRepository,
                              BolinaRepository bolinaRepository) {
        this.activeSprintResolver = activeSprintResolver;
        this.developerRepository = developerRepository;
        this.bolinaRepository = bolinaRepository;
    }

    @Override
    public BufferResponse calcular() {
        Long teamId = CurrentUser.teamId();
        Sprint sprintAtivo = activeSprintResolver.resolve(teamId);

        int capacidadeTotalSprint = developerRepository.sumCapacidadeTotalByTeamId(teamId);
        int bufferReservado = (int) Math.ceil(capacidadeTotalSprint * sprintAtivo.getBufferPercentual() / 100.0);

        int bufferConsumido = bolinaRepository.findAllBySprintIdAndEliminadoFalse(sprintAtivo.getId()).stream()
                .filter(b -> !Boolean.TRUE.equals(b.getFuera()))
                .filter(b -> b.getZona() == ZonaOperativa.CONTINUIDAD_OPERATIVA)
                .mapToInt(b -> b.getTamanho() == null ? 0 : b.getTamanho())
                .sum();

        int bufferDisponivel = bufferReservado - bufferConsumido;

        Double percentualUso;
        SemaforoBuffer semaforo;
        if (bufferReservado == 0) {
            if (bufferConsumido == 0) {
                percentualUso = 0.0;
                semaforo = SemaforoBuffer.VERDE;
            } else {
                percentualUso = null;
                semaforo = SemaforoBuffer.VERMELHO;
            }
        } else {
            percentualUso = Math.round(bufferConsumido * 10000.0 / bufferReservado) / 100.0;
            if (percentualUso < 50.0) {
                semaforo = SemaforoBuffer.VERDE;
            } else if (percentualUso <= 100.0) {
                semaforo = SemaforoBuffer.AMARELO;
            } else {
                semaforo = SemaforoBuffer.VERMELHO;
            }
        }

        return new BufferResponse(capacidadeTotalSprint, sprintAtivo.getBufferPercentual(), bufferReservado,
                bufferConsumido, bufferDisponivel, percentualUso, semaforo);
    }
}
