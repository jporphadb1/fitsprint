package com.jporpha.fitsprint.service;

import com.jporpha.fitsprint.dto.LoginRequest;
import com.jporpha.fitsprint.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
