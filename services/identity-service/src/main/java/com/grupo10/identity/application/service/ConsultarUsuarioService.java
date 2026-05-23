package com.grupo10.identity.application.service;

import com.grupo10.identity.application.port.in.ConsultarUsuarioUseCase;
import com.grupo10.identity.application.port.out.LoadUsuarioPort;
import com.grupo10.identity.domain.exception.UsuarioNoEncontradoException;
import com.grupo10.identity.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultarUsuarioService implements ConsultarUsuarioUseCase {

    private final LoadUsuarioPort loadUsuarioPort;

    public ConsultarUsuarioService(LoadUsuarioPort loadUsuarioPort) {
        this.loadUsuarioPort = loadUsuarioPort;
    }

    @Override
    public Usuario consultarPorId(String id) {
        return loadUsuarioPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Override
    public List<Usuario> listarTodos() {
        return loadUsuarioPort.findAll();
    }
}
