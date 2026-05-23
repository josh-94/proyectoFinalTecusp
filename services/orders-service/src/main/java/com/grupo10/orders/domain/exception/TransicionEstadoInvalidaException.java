package com.grupo10.orders.domain.exception;

import com.grupo10.orders.domain.model.EstadoPedido;

public class TransicionEstadoInvalidaException extends RuntimeException {
    public TransicionEstadoInvalidaException(EstadoPedido actual, EstadoPedido destino) {
        super("No se puede pasar de " + actual + " a " + destino);
    }
}
