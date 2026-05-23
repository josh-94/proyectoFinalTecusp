package com.grupo10.identity.domain.exception;

public class UsuarioYaExisteException extends RuntimeException {

    public UsuarioYaExisteException(String identifier) {
        super("Ya existe un usuario con ese username o email: " + identifier);
    }
}
