package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.UserRequest;
import com.jporpha.fitsprint.dto.UserResponse;
import java.util.List;

/** Gestão global de usuários — SUPER_ADMIN, não acotado a team_id (Sofia_context.md, persona Valentina). */
public interface UserService {

    UserResponse criar(UserRequest request);

    List<UserResponse> listar();

    UserResponse buscarPorId(Long id);

    UserResponse atualizar(Long id, UserRequest request);

    void remover(Long id);
}
