package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import com.jporpha.fitsprint.service.OcupacaoService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Ocupação e avanço. Acesso: USER, ADMIN e SUPER_ADMIN. */
@RestController
@RequestMapping("/api/v1/ocupacao")
@PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
public class OcupacaoController {

    private final OcupacaoService ocupacaoService;

    public OcupacaoController(OcupacaoService ocupacaoService) {
        this.ocupacaoService = ocupacaoService;
    }

    @GetMapping("/mapa")
    public List<BolinaResponse> mapaAlocacao(@RequestParam(required = false) Long teamId) {
        return ocupacaoService.mapaAlocacao(teamId);
    }

    @GetMapping("/disponibilidade")
    public List<DisponibilidadeDeveloperResponse> disponibilidade(@RequestParam(required = false) Long teamId) {
        return ocupacaoService.disponibilidadePorDeveloper(teamId);
    }
}
