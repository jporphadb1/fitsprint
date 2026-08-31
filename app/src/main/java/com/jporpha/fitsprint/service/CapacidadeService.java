package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.CapacidadeConsolidadaResponse;

public interface CapacidadeService {

    /** Capacidade individual por developer e consolidada do team, no sprint ativo. teamId nulo usa o time corrente; SUPER_ADMIN deve informá-lo. */
    CapacidadeConsolidadaResponse consultar(Long teamId);
}
