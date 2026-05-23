package com.grupo10.returns.domain.exception;

public class DevolucionNoEncontradaException extends RuntimeException {
    public DevolucionNoEncontradaException(String devolucionId) {
        super("Devolución no encontrada: " + devolucionId);
    }
}
