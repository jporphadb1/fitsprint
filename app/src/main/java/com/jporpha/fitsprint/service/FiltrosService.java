package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.dto.DisponibilidadeDeveloperResponse;
import com.jporpha.fitsprint.entity.TaskStatus;
import com.jporpha.fitsprint.entity.ZonaOperativa;
import java.util.List;

/** Módulo: Filtros e vistas. Estritamente de consulta — nenhum método aqui grava dado algum. */
public interface FiltrosService {

    /**
     * Vista principal do sprint ativo, já ordenada, com filtros combinados por AND.
     * Combinações sem resultado retornam lista vazia, nunca erro.
     *
     * @param developerId    filtra por responsável específico (ignorado se semResponsavel = true)
     * @param semResponsavel quando true, filtra apenas bolinas sem responsável ("Sin asignar")
     * @param estado         TODO / IN_PROGRESS / DONE
     * @param zona           CONTINUIDAD_OPERATIVA / SPRINT_NORMAL
     */
    List<BolinaResponse> vistaPrincipal(Long developerId, Boolean semResponsavel, TaskStatus estado, ZonaOperativa zona);

    /** Apenas bolinas da zona CONTINUIDAD_OPERATIVA (URGENCIA e BUG_NUEVO) do sprint ativo. */
    List<BolinaResponse> vistaUrgencias();

    /** Delegada ao módulo Ocupação e avanço — não recalcula nada aqui. */
    List<DisponibilidadeDeveloperResponse> vistaDisponibilidade();
}
