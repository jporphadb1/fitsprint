package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import java.util.List;

public interface OcupacaoService {

    /**
     * Mapa de alocação do sprint ativo: todas as bolinas não eliminadas, com responsável (ou "Sin
     * asignar"). teamId nulo usa o time corrente; SUPER_ADMIN deve informá-lo.
     */
    List<BolinaResponse> mapaAlocacao(Long teamId);

    /**
     * Disponibilidade e sinalização de sobrecarga por developer do time.
     * teamId nulo usa o time corrente; SUPER_ADMIN deve informá-lo.
     */
    List<DisponibilidadeDeveloperResponse> disponibilidadePorDeveloper(Long teamId);
}
