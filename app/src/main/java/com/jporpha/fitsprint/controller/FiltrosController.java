package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import com.jporpha.fitsprint.entity.TaskStatus;
import com.jporpha.fitsprint.entity.ZonaOperativa;
import com.jporpha.fitsprint.service.FiltrosService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Filtros e vistas. Estritamente de consulta. Acesso: USER, ADMIN e SUPER_ADMIN. */
@RestController
@RequestMapping("/api/v1/vistas")
@PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
public class FiltrosController {

    private final FiltrosService filtrosService;

    public FiltrosController(FiltrosService filtrosService) {
        this.filtrosService = filtrosService;
    }

    @GetMapping("/principal")
    public List<BolinaResponse> principal(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long developerId,
            @RequestParam(required = false) Boolean semResponsavel,
            @RequestParam(required = false) TaskStatus estado,
            @RequestParam(required = false) ZonaOperativa zona) {
        return filtrosService.vistaPrincipal(teamId, developerId, semResponsavel, estado, zona);
    }

    @GetMapping("/urgencias")
    public List<BolinaResponse> urgencias(@RequestParam(required = false) Long teamId) {
        return filtrosService.vistaUrgencias(teamId);
    }

    @GetMapping("/disponibilidade")
    public List<DisponibilidadeDeveloperResponse> disponibilidade(@RequestParam(required = false) Long teamId) {
        return filtrosService.vistaDisponibilidade(teamId);
    }
}
