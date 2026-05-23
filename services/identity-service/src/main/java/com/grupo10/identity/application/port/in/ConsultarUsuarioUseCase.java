package com.grupo10.identity.application.port.in;

import com.grupo10.identity.domain.model.Usuario;

import java.util.List;

public interface ConsultarUsuarioUseCase {

    Usuario consultarPorId(String id);

    List<Usuario> listarTodos();
}
