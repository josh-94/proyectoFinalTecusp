package com.grupo10.identity.infrastructure.adapter.in.rest.dto;

import com.grupo10.identity.domain.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegistrarUsuarioRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String nombre,
        @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
        @NotEmpty Set<Rol> roles
) {}
