package com.grupo10.orders.application.port.out;

import com.grupo10.orders.domain.model.Pedido;

public interface SavePedidoPort {
    Pedido save(Pedido pedido);
}
