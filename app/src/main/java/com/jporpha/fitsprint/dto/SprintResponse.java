package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.SprintStatus;
import java.time.LocalDateTime;

public record SprintResponse(
        Long id,
        String nome,
        SprintStatus status,
        Integer bufferPercentual,
        Long teamId,
        LocalDateTime dataCriacao) {
}
