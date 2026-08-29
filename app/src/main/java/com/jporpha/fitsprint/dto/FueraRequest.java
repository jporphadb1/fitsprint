package com.jporpha.fitsprint.dto;

import jakarta.validation.constraints.NotNull;

public record FueraRequest(@NotNull Boolean fuera) {
}
