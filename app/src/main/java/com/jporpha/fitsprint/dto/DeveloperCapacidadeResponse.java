package com.jporpha.fitsprint.dto;

public record DeveloperCapacidadeResponse(
        Long developerId,
        String developerNome,
        Integer capacidadeTotal,
        Integer capacidadeUsada,
        Integer capacidadeDisponivel) {
}
