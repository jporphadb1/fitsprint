package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.DeveloperRequest;
import com.jporpha.fitsprint.dto.DeveloperResponse;
import com.jporpha.fitsprint.service.DeveloperService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CRUD de developers do time. Acesso: ADMIN e SUPER_ADMIN (Sofia_context.md, persona Sofía). */
@RestController
@RequestMapping("/api/v1/developers")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class DeveloperController {

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeveloperResponse criar(@Valid @RequestBody DeveloperRequest request) {
        return developerService.criar(request);
    }

    @GetMapping
    public List<DeveloperResponse> listar(@RequestParam(required = false) Long teamId) {
        return developerService.listar(teamId);
    }

    @GetMapping("/{id}")
    public DeveloperResponse buscarPorId(@PathVariable Long id) {
        return developerService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public DeveloperResponse atualizar(@PathVariable Long id, @Valid @RequestBody DeveloperRequest request) {
        return developerService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        developerService.remover(id);
    }
}
