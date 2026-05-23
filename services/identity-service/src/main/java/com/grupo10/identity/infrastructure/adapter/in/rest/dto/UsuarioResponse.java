package com.grupo10.identity.infrastructure.adapter.in.rest.dto;

import com.grupo10.identity.domain.model.Rol;
import com.grupo10.identity.domain.model.Usuario;

import java.time.Instant;
import java.util.Set;

public record UsuarioResponse(
        String id,
        String username,
        String email,
        String nombre,
        Set<Rol> roles,
        boolean activo,
        Instant creadoEn
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRoles(),
                usuario.isActivo(),
                usuario.getCreadoEn()
        );
    }
}
