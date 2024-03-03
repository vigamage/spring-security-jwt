package com.example.sms.security;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAuthService {
    UserDetailsService userDetailsService();
}
