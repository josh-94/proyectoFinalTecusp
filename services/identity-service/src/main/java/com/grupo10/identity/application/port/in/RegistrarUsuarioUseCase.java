package com.grupo10.identity.application.port.in;

import com.grupo10.identity.domain.model.Rol;

import java.time.Instant;
import java.util.Set;

public interface RegistrarUsuarioUseCase {

    UsuarioCreado registrar(RegistrarUsuarioCommand command);

    record RegistrarUsuarioCommand(
            String username,
            String email,
            String nombre,
            String password,
            Set<Rol> roles
    ) {}

    record UsuarioCreado(
            String id,
            String username,
            String email,
            String nombre,
            Set<Rol> roles,
            Instant creadoEn
    ) {}
}
