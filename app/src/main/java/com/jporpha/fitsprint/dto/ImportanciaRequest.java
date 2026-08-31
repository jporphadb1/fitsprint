package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.Importancia;
import jakarta.validation.constraints.NotNull;

public record ImportanciaRequest(@NotNull Importancia importancia) {
}
