package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.UserRequest;
import com.jporpha.fitsprint.dto.UserResponse;
import com.jporpha.fitsprint.entity.Role;
import com.jporpha.fitsprint.entity.Team;
import com.jporpha.fitsprint.entity.User;
import com.jporpha.fitsprint.exception.BusinessRuleException;
import com.jporpha.fitsprint.exception.ResourceNotFoundException;
import com.jporpha.fitsprint.repository.TeamRepository;
import com.jporpha.fitsprint.repository.UserRepository;
import com.jporpha.fitsprint.service.UserService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, TeamRepository teamRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse criar(UserRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessRuleException("Senha é obrigatória na criação do usuário");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessRuleException("Já existe um usuário com este email");
        }

        Team team = resolverTeam(request.role(), request.teamId());

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .team(team)
                .build();

        return toResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> listar() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Override
    public UserResponse atualizar(Long id, UserRequest request) {
        User user = buscarEntidade(id);

        userRepository.findByEmail(request.email())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new BusinessRuleException("Já existe um usuário com este email");
                });

        Team team = resolverTeam(request.role(), request.teamId());

        user.setEmail(request.email());
        user.setRole(request.role());
        user.setTeam(team);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return toResponse(userRepository.save(user));
    }

    @Override
    public void remover(Long id) {
        userRepository.delete(buscarEntidade(id));
    }

    private Team resolverTeam(Role role, Long teamId) {
        if (role == Role.SUPER_ADMIN) {
            return null;
        }
        if (teamId == null) {
            throw new BusinessRuleException("teamId é obrigatório para papéis ADMIN e USER");
        }
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado: " + teamId));
    }

    private User buscarEntidade(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private UserResponse toResponse(User user) {
        Long teamId = user.getTeam() == null ? null : user.getTeam().getId();
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), teamId);
    }
}
