package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.BolinaRequest;
import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.EstadoRequest;
import com.jporpha.fitsprint.dto.FueraRequest;
import com.jporpha.fitsprint.dto.ImportanciaRequest;
import com.jporpha.fitsprint.dto.PrioridadeFinalRequest;
import com.jporpha.fitsprint.dto.ResponsavelRequest;
import com.jporpha.fitsprint.service.BolinaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Módulo: Backlog priorizado.
 *
 * <p>Acesso default (classe): USER, ADMIN e SUPER_ADMIN — cobre criação, leitura, edição de
 * conteúdo (tamanho/valor), estado, fuera, responsável e remoção. Importância e prioridade final
 * manual são decisão de PO/Scrum Master e ficam elevadas a ADMIN/SUPER_ADMIN nos métodos abaixo
 * (per pedido explícito do usuário, mapeado a partir de discovery/persona/Sofia_context.md).
 */
@RestController
@RequestMapping("/api/v1/bolinas")
@PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
public class BolinaController {

    private final BolinaService bolinaService;

    public BolinaController(BolinaService bolinaService) {
        this.bolinaService = bolinaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BolinaResponse criar(@Valid @RequestBody BolinaRequest request) {
        return bolinaService.criar(request);
    }

    @GetMapping
    public List<BolinaResponse> listarPriorizado(@RequestParam(required = false) Long teamId) {
        return bolinaService.listarPriorizado(teamId);
    }

    @GetMapping("/{id}")
    public BolinaResponse buscarPorId(@PathVariable Long id) {
        return bolinaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public BolinaResponse atualizar(@PathVariable Long id, @Valid @RequestBody BolinaRequest request) {
        return bolinaService.atualizar(id, request);
    }

    @PatchMapping("/{id}/importancia")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BolinaResponse atualizarImportancia(@PathVariable Long id, @Valid @RequestBody ImportanciaRequest request) {
        return bolinaService.atualizarImportancia(id, request.importancia());
    }

    @PatchMapping("/{id}/prioridade-final")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BolinaResponse atualizarPrioridadeFinal(@PathVariable Long id, @RequestBody PrioridadeFinalRequest request) {
        return bolinaService.atualizarPrioridadeFinal(id, request.prioridadeFinal());
    }

    @PatchMapping("/{id}/estado")
    public BolinaResponse atualizarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return bolinaService.atualizarEstado(id, request.estado());
    }

    @PatchMapping("/{id}/fuera")
    public BolinaResponse atualizarFuera(@PathVariable Long id, @Valid @RequestBody FueraRequest request) {
        return bolinaService.atualizarFuera(id, request.fuera());
    }

    @PatchMapping("/{id}/responsavel")
    public BolinaResponse atualizarResponsavel(@PathVariable Long id, @RequestBody ResponsavelRequest request) {
        return bolinaService.atualizarResponsavel(id, request.developerId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        bolinaService.remover(id);
    }
}
