package com.grupo10.identity.application.port.in;

public interface AutenticarUseCase {

    TokenPair autenticar(AutenticarCommand command);

    record AutenticarCommand(String username, String password) {}

    record TokenPair(String accessToken, String refreshToken) {}
}
