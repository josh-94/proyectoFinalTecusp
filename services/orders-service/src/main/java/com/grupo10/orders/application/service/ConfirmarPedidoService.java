package com.grupo10.orders.application.service;

import com.grupo10.orders.application.port.in.ConfirmarPedidoUseCase;
import com.grupo10.orders.application.port.out.LoadPedidoPort;
import com.grupo10.orders.application.port.out.SavePedidoPort;
import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import com.grupo10.orders.domain.model.EstadoPedido;
import com.grupo10.orders.domain.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfirmarPedidoService implements ConfirmarPedidoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmarPedidoService.class);

    private final LoadPedidoPort loadPedidoPort;
    private final SavePedidoPort savePedidoPort;

    public ConfirmarPedidoService(LoadPedidoPort loadPedidoPort, SavePedidoPort savePedidoPort) {
        this.loadPedidoPort = loadPedidoPort;
        this.savePedidoPort = savePedidoPort;
    }

    @Override
    public void confirmar(String pedidoId) {
        Pedido pedido = loadPedidoPort.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE_STOCK) {
            log.warn("Pedido {} ya no está en PENDIENTE_STOCK (estado={}), ignorando confirmación",
                    pedidoId, pedido.getEstado());
            return;
        }

        pedido.confirmar();
        savePedidoPort.save(pedido);
        log.info("Pedido {} confirmado", pedidoId);
    }
}
