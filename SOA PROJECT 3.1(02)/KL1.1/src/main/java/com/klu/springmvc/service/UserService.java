package com.klu.springmvc.service;

import com.klu.springmvc.dto.AuthResponse;
import com.klu.springmvc.dto.LoginRequest;
import com.klu.springmvc.dto.RegisterRequest;
import com.klu.springmvc.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
}
