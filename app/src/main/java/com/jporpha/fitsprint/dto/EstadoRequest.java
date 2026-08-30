package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull TaskStatus estado) {
}
