package com.grupo10.identity.application.service;

import com.grupo10.identity.application.port.in.RegistrarUsuarioUseCase;
import com.grupo10.identity.application.port.out.HashPasswordPort;
import com.grupo10.identity.application.port.out.LoadUsuarioPort;
import com.grupo10.identity.application.port.out.SaveUsuarioPort;
import com.grupo10.identity.domain.exception.UsuarioYaExisteException;
import com.grupo10.identity.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {

    private final LoadUsuarioPort loadUsuarioPort;
    private final SaveUsuarioPort saveUsuarioPort;
    private final HashPasswordPort hashPasswordPort;

    public RegistrarUsuarioService(LoadUsuarioPort loadUsuarioPort,
                                   SaveUsuarioPort saveUsuarioPort,
                                   HashPasswordPort hashPasswordPort) {
        this.loadUsuarioPort = loadUsuarioPort;
        this.saveUsuarioPort = saveUsuarioPort;
        this.hashPasswordPort = hashPasswordPort;
    }

    @Override
    public UsuarioCreado registrar(RegistrarUsuarioCommand command) {
        if (loadUsuarioPort.existsByUsername(command.username())) {
            throw new UsuarioYaExisteException(command.username());
        }
        if (loadUsuarioPort.existsByEmail(command.email())) {
            throw new UsuarioYaExisteException(command.email());
        }

        var usuario = new Usuario(
                UUID.randomUUID().toString(),
                command.username(),
                command.email(),
                command.nombre(),
                hashPasswordPort.hash(command.password()),
                command.roles(),
                true,
                Instant.now()
        );

        var saved = saveUsuarioPort.save(usuario);
        return new UsuarioCreado(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getNombre(),
                saved.getRoles(),
                saved.getCreadoEn()
        );
    }
}
