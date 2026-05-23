package com.grupo10.identity.domain.model;

import com.grupo10.identity.domain.exception.TokenInvalidoException;

import java.time.Instant;

public class RefreshToken {

    private final String id;
    private final String usuarioId;
    private final String tokenHash;
    private final Instant expiresAt;
    private boolean revocado;

    public RefreshToken(String id, String usuarioId, String tokenHash,
                        Instant expiresAt, boolean revocado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revocado = revocado;
    }

    public void validar() {
        if (revocado) {
            throw new TokenInvalidoException("El refresh token ha sido revocado");
        }
        if (Instant.now().isAfter(expiresAt)) {
            throw new TokenInvalidoException("El refresh token ha expirado");
        }
    }

    public void revocar() {
        this.revocado = true;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevocado() { return revocado; }
}
