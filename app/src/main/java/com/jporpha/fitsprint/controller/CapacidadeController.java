package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.CapacidadeConsolidadaResponse;
import com.jporpha.fitsprint.service.CapacidadeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Capacidade do time. */
@RestController
@RequestMapping("/api/v1/capacidade")
public class CapacidadeController {

    private final CapacidadeService capacidadeService;

    public CapacidadeController(CapacidadeService capacidadeService) {
        this.capacidadeService = capacidadeService;
    }

    @GetMapping
    public CapacidadeConsolidadaResponse consultar() {
        return capacidadeService.consultar();
    }
}
