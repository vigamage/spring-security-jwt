package com.example.sms.security.auth;

import com.example.sms.dto.auth.JwtAuthenticationResponse;
import com.example.sms.dto.auth.SignInRequest;
import com.example.sms.dto.auth.SignUpRequest;

public interface AuthService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SignInRequest request);
}
