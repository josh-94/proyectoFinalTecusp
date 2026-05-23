package com.grupo10.orders.application.service;

import com.grupo10.orders.application.port.in.CancelarPedidoUseCase;
import com.grupo10.orders.application.port.out.LoadPedidoPort;
import com.grupo10.orders.application.port.out.SavePedidoPort;
import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import com.grupo10.orders.domain.model.Pedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CancelarPedidoService implements CancelarPedidoUseCase {

    private final LoadPedidoPort loadPedidoPort;
    private final SavePedidoPort savePedidoPort;

    public CancelarPedidoService(LoadPedidoPort loadPedidoPort, SavePedidoPort savePedidoPort) {
        this.loadPedidoPort = loadPedidoPort;
        this.savePedidoPort = savePedidoPort;
    }

    @Override
    public void cancelar(String pedidoId) {
        Pedido pedido = loadPedidoPort.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));
        pedido.cancelar();
        savePedidoPort.save(pedido);
    }
}
