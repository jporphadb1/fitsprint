package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.BufferResponse;

public interface BufferService {

    /** Buffer de continuidade operativa do sprint ativo. teamId nulo usa o time corrente; SUPER_ADMIN deve informá-lo. */
    BufferResponse calcular(Long teamId);
}
