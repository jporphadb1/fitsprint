package com.jporpha.fitsprint.dto;

import java.util.List;

public record CapacidadeConsolidadaResponse(
        Integer capacidadeTotalTime,
        Integer capacidadeUsadaTime,
        Integer capacidadeDisponivelTime,
        List<DeveloperCapacidadeResponse> porDeveloper) {
}
