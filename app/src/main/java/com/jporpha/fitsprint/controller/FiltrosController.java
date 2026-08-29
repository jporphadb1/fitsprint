package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import com.jporpha.fitsprint.entity.TaskStatus;
import com.jporpha.fitsprint.entity.ZonaOperativa;
import com.jporpha.fitsprint.service.FiltrosService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Filtros e vistas. Estritamente de consulta. */
@RestController
@RequestMapping("/api/v1/vistas")
public class FiltrosController {

    private final FiltrosService filtrosService;

    public FiltrosController(FiltrosService filtrosService) {
        this.filtrosService = filtrosService;
    }

    @GetMapping("/principal")
    public List<BolinaResponse> principal(
            @RequestParam(required = false) Long developerId,
            @RequestParam(required = false) Boolean semResponsavel,
            @RequestParam(required = false) TaskStatus estado,
            @RequestParam(required = false) ZonaOperativa zona) {
        return filtrosService.vistaPrincipal(developerId, semResponsavel, estado, zona);
    }

    @GetMapping("/urgencias")
    public List<BolinaResponse> urgencias() {
        return filtrosService.vistaUrgencias();
    }

    @GetMapping("/disponibilidade")
    public List<DisponibilidadeDeveloperResponse> disponibilidade() {
        return filtrosService.vistaDisponibilidade();
    }
}
