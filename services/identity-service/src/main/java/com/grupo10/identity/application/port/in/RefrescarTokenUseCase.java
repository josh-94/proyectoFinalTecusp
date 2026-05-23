package com.grupo10.identity.application.port.in;

public interface RefrescarTokenUseCase {

    NuevoAccessToken refrescar(String refreshToken);

    record NuevoAccessToken(String accessToken) {}
}
