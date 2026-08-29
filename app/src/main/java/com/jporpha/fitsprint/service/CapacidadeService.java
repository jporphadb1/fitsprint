package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.CapacidadeConsolidadaResponse;

public interface CapacidadeService {

    /** Capacidade individual por developer e consolidada do team, no sprint ativo. */
    CapacidadeConsolidadaResponse consultar();
}
