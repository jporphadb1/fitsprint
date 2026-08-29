package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.BufferResponse;

public interface BufferService {

    /** Buffer de continuidade operativa do sprint ativo do time corrente. */
    BufferResponse calcular();
}
