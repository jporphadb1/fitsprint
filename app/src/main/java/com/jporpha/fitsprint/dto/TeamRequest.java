package com.jporpha.fitsprint.dto;

import jakarta.validation.constraints.NotBlank;

public record TeamRequest(@NotBlank String nome) {
}
