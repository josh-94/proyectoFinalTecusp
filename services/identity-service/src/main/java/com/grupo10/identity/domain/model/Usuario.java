package com.grupo10.identity.domain.model;

import java.time.Instant;
import java.util.Set;

public class Usuario {

    private final String id;
    private final String username;
    private final String email;
    private final String nombre;
    private final String passwordHash;
    private final Set<Rol> roles;
    private final boolean activo;
    private final Instant creadoEn;

    public Usuario(String id, String username, String email, String nombre,
                   String passwordHash, Set<Rol> roles, boolean activo, Instant creadoEn) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.roles = Set.copyOf(roles);
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean tieneRol(Rol rol) {
        return roles.contains(rol);
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getPasswordHash() { return passwordHash; }
    public Set<Rol> getRoles() { return roles; }
    public boolean isActivo() { return activo; }
    public Instant getCreadoEn() { return creadoEn; }
}
