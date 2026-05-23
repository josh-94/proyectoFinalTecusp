package com.grupo10.orders.domain.exception;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(String pedidoId) {
        super("Pedido no encontrado: " + pedidoId);
    }
}
