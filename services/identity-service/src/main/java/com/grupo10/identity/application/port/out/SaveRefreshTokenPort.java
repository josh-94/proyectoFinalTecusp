package com.grupo10.identity.application.port.out;

import com.grupo10.identity.domain.model.RefreshToken;

public interface SaveRefreshTokenPort {

    void save(RefreshToken token);
}
