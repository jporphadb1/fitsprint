package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.CapacidadeConsolidadaResponse;
import com.jporpha.fitsprint.service.CapacidadeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Capacidade do time. Acesso: USER, ADMIN e SUPER_ADMIN. */
@RestController
@RequestMapping("/api/v1/capacidade")
@PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
public class CapacidadeController {

    private final CapacidadeService capacidadeService;

    public CapacidadeController(CapacidadeService capacidadeService) {
        this.capacidadeService = capacidadeService;
    }

    @GetMapping
    public CapacidadeConsolidadaResponse consultar(@RequestParam(required = false) Long teamId) {
        return capacidadeService.consultar(teamId);
    }
}
