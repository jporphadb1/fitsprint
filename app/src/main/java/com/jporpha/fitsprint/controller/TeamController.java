package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.TeamRequest;
import com.jporpha.fitsprint.dto.TeamResponse;
import com.jporpha.fitsprint.service.TeamService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Gestão global de equipes. Acesso: exclusivo SUPER_ADMIN (Sofia_context.md, persona Valentina). */
@RestController
@RequestMapping("/api/v1/teams")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse criar(@Valid @RequestBody TeamRequest request) {
        return teamService.criar(request);
    }

    @GetMapping
    public List<TeamResponse> listar() {
        return teamService.listar();
    }

    @GetMapping("/{id}")
    public TeamResponse buscarPorId(@PathVariable Long id) {
        return teamService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TeamResponse atualizar(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        return teamService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        teamService.remover(id);
    }
}
