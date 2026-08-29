package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.BolinaRequest;
import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.entity.Importancia;
import com.jporpha.fitsprint.entity.TaskStatus;
import java.util.List;

public interface BolinaService {

    BolinaResponse criar(BolinaRequest request);

    /**
     * Backlog priorizado do sprint ativo, já ordenado (sem filtros).
     * {@code teamId} nulo usa o time do usuário corrente; SUPER_ADMIN deve informá-lo.
     */
    List<BolinaResponse> listarPriorizado(Long teamId);

    BolinaResponse buscarPorId(Long id);

    BolinaResponse atualizar(Long id, BolinaRequest request);

    BolinaResponse atualizarImportancia(Long id, Importancia importancia);

    BolinaResponse atualizarPrioridadeFinal(Long id, Integer prioridadeFinal);

    BolinaResponse atualizarEstado(Long id, TaskStatus estado);

    BolinaResponse atualizarFuera(Long id, boolean fuera);

    /**
     * Atribui, reatribui ou desatribui (developerId nulo) o responsável.
     * Módulo Ocupação e avanço: ADMIN e USER podem atribuir, sem exclusividade administrativa.
     */
    BolinaResponse atualizarResponsavel(Long id, Long developerId);

    void remover(Long id);
}
