package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.Importancia;
import com.jporpha.fitsprint.entity.TaskStatus;
import com.jporpha.fitsprint.entity.TaskType;
import com.jporpha.fitsprint.entity.ZonaOperativa;
import java.time.LocalDateTime;

public record BolinaResponse(
        Long id,
        String titulo,
        Integer tamanho,
        Integer valor,
        double ratio,
        TaskType tipo,
        ZonaOperativa zona,
        Importancia importancia,
        Integer prioridadeFinal,
        TaskStatus estado,
        Long developerId,
        String developerNome,
        Boolean fuera,
        LocalDateTime dataCriacao) {
}
