package com.grupo10.identity.application.service;

import com.grupo10.identity.application.port.in.RefrescarTokenUseCase;
import com.grupo10.identity.application.port.out.GenerarJwtPort;
import com.grupo10.identity.application.port.out.LoadRefreshTokenPort;
import com.grupo10.identity.application.port.out.LoadUsuarioPort;
import com.grupo10.identity.domain.exception.TokenInvalidoException;
import com.grupo10.identity.domain.exception.UsuarioNoEncontradoException;
import com.grupo10.identity.domain.model.RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RefrescarTokenService implements RefrescarTokenUseCase {

    private final LoadRefreshTokenPort loadRefreshTokenPort;
    private final LoadUsuarioPort loadUsuarioPort;
    private final GenerarJwtPort generarJwtPort;

    public RefrescarTokenService(LoadRefreshTokenPort loadRefreshTokenPort,
                                 LoadUsuarioPort loadUsuarioPort,
                                 GenerarJwtPort generarJwtPort) {
        this.loadRefreshTokenPort = loadRefreshTokenPort;
        this.loadUsuarioPort = loadUsuarioPort;
        this.generarJwtPort = generarJwtPort;
    }

    @Override
    public NuevoAccessToken refrescar(String rawRefreshToken) {
        String tokenHash = AutenticarService.sha256(rawRefreshToken);

        RefreshToken refreshToken = loadRefreshTokenPort.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenInvalidoException("Refresh token no encontrado"));

        refreshToken.validar();

        var usuario = loadUsuarioPort.findById(refreshToken.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(refreshToken.getUsuarioId()));

        return new NuevoAccessToken(generarJwtPort.generarAccessToken(usuario));
    }
}
