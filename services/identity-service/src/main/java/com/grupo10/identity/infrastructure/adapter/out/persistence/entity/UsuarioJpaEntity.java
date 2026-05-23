package com.grupo10.identity.infrastructure.adapter.out.persistence.entity;

import com.grupo10.identity.domain.model.Rol;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 50)
    private Set<Rol> roles = new HashSet<>();

    protected UsuarioJpaEntity() {}

    public UsuarioJpaEntity(String id, String username, String email, String nombre,
                             String passwordHash, boolean activo, Instant creadoEn, Set<Rol> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.activo = activo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = creadoEn;
        this.roles = new HashSet<>(roles);
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isActivo() { return activo; }
    public Instant getCreadoEn() { return creadoEn; }
    public Set<Rol> getRoles() { return roles; }
}
