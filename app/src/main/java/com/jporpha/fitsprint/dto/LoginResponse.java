package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.Role;

public record LoginResponse(String token, String email, Role role, Long teamId) {
}
