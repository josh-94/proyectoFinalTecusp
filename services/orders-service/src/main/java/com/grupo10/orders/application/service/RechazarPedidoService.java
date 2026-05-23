package com.grupo10.orders.application.service;

import com.grupo10.orders.application.port.in.RechazarPedidoUseCase;
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
public class RechazarPedidoService implements RechazarPedidoUseCase {

    private static final Logger log = LoggerFactory.getLogger(RechazarPedidoService.class);

    private final LoadPedidoPort loadPedidoPort;
    private final SavePedidoPort savePedidoPort;

    public RechazarPedidoService(LoadPedidoPort loadPedidoPort, SavePedidoPort savePedidoPort) {
        this.loadPedidoPort = loadPedidoPort;
        this.savePedidoPort = savePedidoPort;
    }

    @Override
    public void rechazar(String pedidoId, String motivo) {
        Pedido pedido = loadPedidoPort.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE_STOCK) {
            log.warn("Pedido {} ya no está en PENDIENTE_STOCK (estado={}), ignorando rechazo",
                    pedidoId, pedido.getEstado());
            return;
        }

        pedido.rechazar(motivo);
        savePedidoPort.save(pedido);
        log.info("Pedido {} rechazado: {}", pedidoId, motivo);
    }
}
