package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.TeamRequest;
import com.jporpha.fitsprint.dto.TeamResponse;
import java.util.List;

/** Gestão global de equipes — SUPER_ADMIN, não acotado a team_id (Sofia_context.md, persona Valentina). */
public interface TeamService {

    TeamResponse criar(TeamRequest request);

    List<TeamResponse> listar();

    TeamResponse buscarPorId(Long id);

    TeamResponse atualizar(Long id, TeamRequest request);

    void remover(Long id);
}
