package com.grupo10.orders.application.port.in;

import com.grupo10.orders.domain.model.LineaDePedido;

import java.util.List;

public interface CrearPedidoUseCase {

    record CrearPedidoCommand(
            String solicitadoPor,
            String hospitalDestino,
            List<LineaDePedido> lineas
    ) {}

    record PedidoCreado(String pedidoId, String numeroPedido) {}

    PedidoCreado crear(CrearPedidoCommand command);
}
