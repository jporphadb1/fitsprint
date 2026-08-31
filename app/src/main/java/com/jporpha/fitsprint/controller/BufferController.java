package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.BufferResponse;
import com.jporpha.fitsprint.service.BufferService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Buffer e urgências. Acesso: USER, ADMIN e SUPER_ADMIN. */
@RestController
@RequestMapping("/api/v1/buffer")
@PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
public class BufferController {

    private final BufferService bufferService;

    public BufferController(BufferService bufferService) {
        this.bufferService = bufferService;
    }

    @GetMapping
    public BufferResponse calcular(@RequestParam(required = false) Long teamId) {
        return bufferService.calcular(teamId);
    }
}
