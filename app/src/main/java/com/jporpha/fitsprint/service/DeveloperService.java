package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.DeveloperRequest;
import com.jporpha.fitsprint.dto.DeveloperResponse;
import java.util.List;

/** CRUD de developers do time. Acesso: ADMIN e SUPER_ADMIN (Sofia_context.md, persona Sofía). */
public interface DeveloperService {

    DeveloperResponse criar(DeveloperRequest request);

    /** {@code teamId} nulo usa o time do usuário corrente; SUPER_ADMIN deve informá-lo. */
    List<DeveloperResponse> listar(Long teamId);

    DeveloperResponse buscarPorId(Long id);

    DeveloperResponse atualizar(Long id, DeveloperRequest request);

    void remover(Long id);
}
