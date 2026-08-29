package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.SprintCreateRequest;
import com.jporpha.fitsprint.dto.SprintResponse;
import com.jporpha.fitsprint.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Create/activate/close de sprints do time. Acesso: ADMIN e SUPER_ADMIN (Sofia_context.md, persona Sofía). */
@RestController
@RequestMapping("/api/v1/sprints")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SprintResponse criar(@Valid @RequestBody SprintCreateRequest request) {
        return sprintService.criar(request);
    }

    @PatchMapping("/{id}/ativar")
    public SprintResponse ativar(@PathVariable Long id) {
        return sprintService.ativar(id);
    }

    @PatchMapping("/{id}/fechar")
    public SprintResponse fechar(@PathVariable Long id) {
        return sprintService.fechar(id);
    }
}
