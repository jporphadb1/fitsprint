package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import java.util.List;

public interface OcupacaoService {

    /** Mapa de alocação do sprint ativo: todas as bolinas não eliminadas, com responsável (ou "Sin asignar"). */
    List<BolinaResponse> mapaAlocacao();

    /** Disponibilidade e sinalização de sobrecarga por developer do time corrente. */
    List<DisponibilidadeDeveloperResponse> disponibilidadePorDeveloper();
}
