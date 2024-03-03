package com.example.sms.controller.auth;

import com.example.sms.domain.operator.OperatorService;
import com.example.sms.dto.ResponseWrapper;
import com.example.sms.dto.auth.JwtAuthenticationResponse;
import com.example.sms.dto.auth.SignInRequest;
import com.example.sms.dto.auth.SignUpRequest;
import com.example.sms.security.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final OperatorService operatorService;

    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<JwtAuthenticationResponse>> signup(@RequestBody SignUpRequest request) {
        JwtAuthenticationResponse signupToken = authService.signup(request);
        ResponseWrapper<JwtAuthenticationResponse> responseWrapper = ResponseWrapper
                .<JwtAuthenticationResponse>builder().result(signupToken).success(true).build();
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseWrapper<JwtAuthenticationResponse>> signin(@RequestBody SignInRequest request) {
        JwtAuthenticationResponse signInToken = authService.signin(request);
        ResponseWrapper<JwtAuthenticationResponse> responseWrapper = ResponseWrapper
                .<JwtAuthenticationResponse>builder().result(signInToken).success(true).build();
        return ResponseEntity.ok(responseWrapper);
    }

}
