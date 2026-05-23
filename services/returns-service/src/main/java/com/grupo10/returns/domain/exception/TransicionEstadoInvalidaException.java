package com.grupo10.returns.domain.exception;

import com.grupo10.returns.domain.model.EstadoDevolucion;

public class TransicionEstadoInvalidaException extends RuntimeException {
    public TransicionEstadoInvalidaException(EstadoDevolucion actual, EstadoDevolucion destino) {
        super("No se puede pasar de " + actual + " a " + destino);
    }
}
