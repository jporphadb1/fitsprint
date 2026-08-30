package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.StatusOcupacao;

public record DisponibilidadeDeveloperResponse(
        Long developerId,
        String developerNome,
        Integer capacidadeTotal,
        Integer ocupacaoAtual,
        Integer capacidadeDisponivel,
        Integer quantidadeTarefas,
        StatusOcupacao status) {
}
