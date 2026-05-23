package com.grupo10.identity.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String identifier) {
        super("Usuario no encontrado: " + identifier);
    }
}
