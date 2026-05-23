package com.grupo10.orders.application.port.in;

public interface RechazarPedidoUseCase {
    void rechazar(String pedidoId, String motivo);
}
