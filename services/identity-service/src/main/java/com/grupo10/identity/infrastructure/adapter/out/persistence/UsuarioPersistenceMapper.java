package com.grupo10.identity.infrastructure.adapter.out.persistence;

import com.grupo10.identity.domain.model.RefreshToken;
import com.grupo10.identity.domain.model.Usuario;
import com.grupo10.identity.infrastructure.adapter.out.persistence.entity.RefreshTokenJpaEntity;
import com.grupo10.identity.infrastructure.adapter.out.persistence.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPersistenceMapper {

    public Usuario toDomain(UsuarioJpaEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getNombre(),
                entity.getPasswordHash(),
                entity.getRoles(),
                entity.isActivo(),
                entity.getCreadoEn()
        );
    }

    public UsuarioJpaEntity toEntity(Usuario usuario) {
        return new UsuarioJpaEntity(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getPasswordHash(),
                usuario.isActivo(),
                usuario.getCreadoEn(),
                usuario.getRoles()
        );
    }

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevocado()
        );
    }

    public RefreshTokenJpaEntity toEntity(RefreshToken token) {
        return new RefreshTokenJpaEntity(
                token.getId(),
                token.getUsuarioId(),
                token.getTokenHash(),
                token.getExpiresAt(),
                token.isRevocado()
        );
    }
}
