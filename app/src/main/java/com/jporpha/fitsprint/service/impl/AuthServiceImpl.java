package com.jporpha.fitsprint.service.impl;

import com.jporpha.fitsprint.dto.LoginRequest;
import com.jporpha.fitsprint.dto.LoginResponse;
import com.jporpha.fitsprint.entity.User;
import com.jporpha.fitsprint.repository.UserRepository;
import com.jporpha.fitsprint.security.AuthenticatedUser;
import com.jporpha.fitsprint.security.JwtTokenProvider;
import com.jporpha.fitsprint.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email ou senha inválidos");
        }

        Long teamId = user.getTeam() == null ? null : user.getTeam().getId();
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), user.getEmail(), user.getRole(), teamId);
        String token = tokenProvider.generateToken(authenticatedUser);

        return new LoginResponse(token, user.getEmail(), user.getRole(), teamId);
    }
}
