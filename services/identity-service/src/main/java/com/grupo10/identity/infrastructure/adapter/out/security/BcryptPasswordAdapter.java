package com.grupo10.identity.infrastructure.adapter.out.security;

import com.grupo10.identity.application.port.out.HashPasswordPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordAdapter implements HashPasswordPort {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean verify(String rawPassword, String hash) {
        return passwordEncoder.matches(rawPassword, hash);
    }
}
