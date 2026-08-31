package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.SemaforoBuffer;

public record BufferResponse(
        Integer capacidadeTotalSprint,
        Integer bufferPercentualConfigurado,
        Integer bufferReservado,
        Integer bufferConsumido,
        Integer bufferDisponivel,
        Double percentualUso,
        SemaforoBuffer semaforo) {
}
