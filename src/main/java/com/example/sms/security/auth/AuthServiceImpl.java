package com.example.sms.security.auth;

import com.example.sms.domain.operator.Operator;
import com.example.sms.domain.operator.OperatorRepository;
import com.example.sms.domain.operator.OperatorRole;
import com.example.sms.dto.auth.JwtAuthenticationResponse;
import com.example.sms.dto.auth.SignInRequest;
import com.example.sms.dto.auth.SignUpRequest;
import com.example.sms.exception.ErrorEnum;
import com.example.sms.exception.SmsException;
import com.example.sms.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final OperatorRepository operatorRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {

        Optional<Operator> operatorOpt = operatorRepository.findByUsername(request.getEmail());
        if (operatorOpt.isPresent()) {
            throw new SmsException(ErrorEnum.OPERATOR_EXISTS, String.format("Operator %s already exists",
                    request.getEmail()));
        }

        var bcryptedPassword = passwordEncoder.encode(request.getPassword());

        Operator operator = Operator.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(request.getFirstName()).lastName(request.getLastName())
                .username(request.getEmail()).password(bcryptedPassword)
                .role(OperatorRole.ADMIN).build();

        operatorRepository.save(operator);
        String jwt = jwtService.createAccessJwtToken(operator);
        String refreshToken = jwtService.createRefreshToken(operator);
        return JwtAuthenticationResponse.builder().accessToken(jwt).refreshToken(refreshToken).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SignInRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var operator = operatorRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new SmsException("User Not Found: " + request.getEmail()));
        String jwt = jwtService.createAccessJwtToken(operator);
        String refreshToken = jwtService.createRefreshToken(operator);
        return JwtAuthenticationResponse.builder().accessToken(jwt).refreshToken(refreshToken).build();
    }
}
