package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import com.jporpha.fitsprint.service.OcupacaoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Ocupação e avanço. */
@RestController
@RequestMapping("/api/v1/ocupacao")
public class OcupacaoController {

    private final OcupacaoService ocupacaoService;

    public OcupacaoController(OcupacaoService ocupacaoService) {
        this.ocupacaoService = ocupacaoService;
    }

    @GetMapping("/mapa")
    public List<BolinaResponse> mapaAlocacao() {
        return ocupacaoService.mapaAlocacao();
    }

    @GetMapping("/disponibilidade")
    public List<DisponibilidadeDeveloperResponse> disponibilidade() {
        return ocupacaoService.disponibilidadePorDeveloper();
    }
}
