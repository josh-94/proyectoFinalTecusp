package com.grupo10.identity.application.port.out;

import com.grupo10.identity.domain.model.RefreshToken;

import java.util.Optional;

public interface LoadRefreshTokenPort {

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
