package com.grupo10.identity.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity {

    @Id
    private String id;

    @Column(name = "usuario_id", nullable = false, length = 36)
    private String usuarioId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revocado = false;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    protected RefreshTokenJpaEntity() {}

    public RefreshTokenJpaEntity(String id, String usuarioId, String tokenHash,
                                  Instant expiresAt, boolean revocado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revocado = revocado;
        this.creadoEn = Instant.now();
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevocado() { return revocado; }
    public Instant getCreadoEn() { return creadoEn; }
}
