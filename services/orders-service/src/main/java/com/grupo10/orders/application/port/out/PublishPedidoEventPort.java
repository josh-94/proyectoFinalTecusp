package com.grupo10.orders.application.port.out;

import com.grupo10.orders.domain.event.PedidoCreadoEvent;

public interface PublishPedidoEventPort {
    void publish(PedidoCreadoEvent event);
}
