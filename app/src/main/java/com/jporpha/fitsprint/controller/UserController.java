package com.jporpha.fitsprint.controller;

import com.jporpha.fitsprint.dto.UserRequest;
import com.jporpha.fitsprint.dto.UserResponse;
import com.jporpha.fitsprint.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Gestão global de usuários. Acesso: exclusivo SUPER_ADMIN (Sofia_context.md, persona Valentina). */
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse criar(@Valid @RequestBody UserRequest request) {
        return userService.criar(request);
    }

    @GetMapping
    public List<UserResponse> listar() {
        return userService.listar();
    }

    @GetMapping("/{id}")
    public UserResponse buscarPorId(@PathVariable Long id) {
        return userService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UserResponse atualizar(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        userService.remover(id);
    }
}
