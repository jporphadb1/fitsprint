package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.BufferResponse;
import com.jporpha.fitsprint.service.BufferService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Módulo: Buffer e urgências. */
@RestController
@RequestMapping("/api/v1/buffer")
public class BufferController {

    private final BufferService bufferService;

    public BufferController(BufferService bufferService) {
        this.bufferService = bufferService;
    }

    @GetMapping
    public BufferResponse calcular() {
        return bufferService.calcular();
    }
}
