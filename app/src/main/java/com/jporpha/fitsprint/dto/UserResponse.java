package com.jporpha.fitsprint.dto;

import com.jporpha.fitsprint.entity.Role;

public record UserResponse(Long id, String email, Role role, Long teamId) {
}
