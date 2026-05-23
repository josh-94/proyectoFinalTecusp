package com.grupo10.identity.application.port.out;

import com.grupo10.identity.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface LoadUsuarioPort {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findById(String id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Usuario> findAll();
}
