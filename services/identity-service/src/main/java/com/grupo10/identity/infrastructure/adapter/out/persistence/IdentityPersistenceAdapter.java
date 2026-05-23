package com.grupo10.identity.infrastructure.adapter.out.persistence;

import com.grupo10.identity.application.port.out.LoadRefreshTokenPort;
import com.grupo10.identity.application.port.out.LoadUsuarioPort;
import com.grupo10.identity.application.port.out.SaveRefreshTokenPort;
import com.grupo10.identity.application.port.out.SaveUsuarioPort;
import com.grupo10.identity.domain.model.RefreshToken;
import com.grupo10.identity.domain.model.Usuario;
import com.grupo10.identity.infrastructure.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import com.grupo10.identity.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IdentityPersistenceAdapter
        implements LoadUsuarioPort, SaveUsuarioPort, LoadRefreshTokenPort, SaveRefreshTokenPort {

    private final UsuarioJpaRepository usuarioRepo;
    private final RefreshTokenJpaRepository refreshTokenRepo;
    private final UsuarioPersistenceMapper mapper;

    public IdentityPersistenceAdapter(UsuarioJpaRepository usuarioRepo,
                                      RefreshTokenJpaRepository refreshTokenRepo,
                                      UsuarioPersistenceMapper mapper) {
        this.usuarioRepo = usuarioRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.mapper = mapper;
    }

    // ── LoadUsuarioPort ──────────────────────────────────────────────────

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepo.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> findById(String id) {
        return usuarioRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usuarioRepo.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepo.existsByEmail(email);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepo.findAll().stream().map(mapper::toDomain).toList();
    }

    // ── SaveUsuarioPort ──────────────────────────────────────────────────

    @Override
    public Usuario save(Usuario usuario) {
        var saved = usuarioRepo.save(mapper.toEntity(usuario));
        return mapper.toDomain(saved);
    }

    // ── LoadRefreshTokenPort ─────────────────────────────────────────────

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenRepo.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    // ── SaveRefreshTokenPort ─────────────────────────────────────────────

    @Override
    public void save(RefreshToken token) {
        refreshTokenRepo.save(mapper.toEntity(token));
    }
}
