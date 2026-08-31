package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.SprintCreateRequest;
import com.jporpha.fitsprint.dto.SprintResponse;

/** Create/activate/close de sprints do time. Acesso: ADMIN e SUPER_ADMIN (Sofia_context.md, persona Sofía). */
public interface SprintService {

    SprintResponse criar(SprintCreateRequest request);

    /** Ativa o sprint; o que estiver ACTIVE no mesmo time passa a CLOSED automaticamente. */
    SprintResponse ativar(Long id);

    SprintResponse fechar(Long id);
}
