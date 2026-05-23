package com.grupo10.identity.application.service;

import com.grupo10.identity.application.port.in.AutenticarUseCase;
import com.grupo10.identity.application.port.out.GenerarJwtPort;
import com.grupo10.identity.application.port.out.HashPasswordPort;
import com.grupo10.identity.application.port.out.LoadUsuarioPort;
import com.grupo10.identity.application.port.out.SaveRefreshTokenPort;
import com.grupo10.identity.domain.exception.CredencialesInvalidasException;
import com.grupo10.identity.domain.model.RefreshToken;
import com.grupo10.identity.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Transactional
public class AutenticarService implements AutenticarUseCase {

    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;

    private final LoadUsuarioPort loadUsuarioPort;
    private final HashPasswordPort hashPasswordPort;
    private final GenerarJwtPort generarJwtPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;

    public AutenticarService(LoadUsuarioPort loadUsuarioPort,
                             HashPasswordPort hashPasswordPort,
                             GenerarJwtPort generarJwtPort,
                             SaveRefreshTokenPort saveRefreshTokenPort) {
        this.loadUsuarioPort = loadUsuarioPort;
        this.hashPasswordPort = hashPasswordPort;
        this.generarJwtPort = generarJwtPort;
        this.saveRefreshTokenPort = saveRefreshTokenPort;
    }

    @Override
    public TokenPair autenticar(AutenticarCommand command) {
        Usuario usuario = loadUsuarioPort.findByUsername(command.username())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!usuario.estaActivo()) {
            throw new CredencialesInvalidasException();
        }

        if (!hashPasswordPort.verify(command.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        String accessToken = generarJwtPort.generarAccessToken(usuario);
        String rawRefreshToken = generarYPersistirRefreshToken(usuario.getId());

        return new TokenPair(accessToken, rawRefreshToken);
    }

    private String generarYPersistirRefreshToken(String usuarioId) {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        var refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                usuarioId,
                sha256(rawToken),
                Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS),
                false
        );
        saveRefreshTokenPort.save(refreshToken);
        return rawToken;
    }

    static String sha256(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Error calculando SHA-256", e);
        }
    }
}
