package com.grupo10.orders.application.service;

import com.grupo10.orders.application.port.in.ConsultarPedidoUseCase;
import com.grupo10.orders.application.port.out.LoadPedidoPort;
import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import com.grupo10.orders.domain.model.Pedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultarPedidoService implements ConsultarPedidoUseCase {

    private final LoadPedidoPort loadPedidoPort;

    public ConsultarPedidoService(LoadPedidoPort loadPedidoPort) {
        this.loadPedidoPort = loadPedidoPort;
    }

    @Override
    public Pedido consultarPorId(String pedidoId) {
        return loadPedidoPort.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));
    }

    @Override
    public List<Pedido> consultarTodos() {
        return loadPedidoPort.findAll();
    }

    @Override
    public List<Pedido> consultarPorSolicitante(String solicitadoPor) {
        return loadPedidoPort.findBySolicitadoPor(solicitadoPor);
    }
}
