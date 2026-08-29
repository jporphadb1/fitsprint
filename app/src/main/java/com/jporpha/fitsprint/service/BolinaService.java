package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.BolinaRequest;
import com.jporpha.fitsprint.dto.BolinaResponse;
import com.jporpha.fitsprint.entity.Importancia;
import com.jporpha.fitsprint.entity.TaskStatus;
import java.util.List;

public interface BolinaService {

    BolinaResponse criar(BolinaRequest request);

    /** Backlog priorizado do sprint ativo do time corrente, já ordenado (sem filtros). */
    List<BolinaResponse> listarPriorizado();

    BolinaResponse buscarPorId(Long id);

    BolinaResponse atualizar(Long id, BolinaRequest request);

    BolinaResponse atualizarImportancia(Long id, Importancia importancia);

    BolinaResponse atualizarPrioridadeFinal(Long id, Integer prioridadeFinal);

    BolinaResponse atualizarEstado(Long id, TaskStatus estado);

    BolinaResponse atualizarFuera(Long id, boolean fuera);

    void remover(Long id);
}
