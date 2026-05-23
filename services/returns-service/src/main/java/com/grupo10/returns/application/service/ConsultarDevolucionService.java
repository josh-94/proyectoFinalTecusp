package com.grupo10.returns.application.service;

import com.grupo10.returns.application.port.in.ConsultarDevolucionUseCase;
import com.grupo10.returns.application.port.out.LoadDevolucionPort;
import com.grupo10.returns.domain.exception.DevolucionNoEncontradaException;
import com.grupo10.returns.domain.model.Devolucion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultarDevolucionService implements ConsultarDevolucionUseCase {

    private final LoadDevolucionPort loadDevolucionPort;

    public ConsultarDevolucionService(LoadDevolucionPort loadDevolucionPort) {
        this.loadDevolucionPort = loadDevolucionPort;
    }

    @Override
    public Devolucion consultarPorId(String devolucionId) {
        return loadDevolucionPort.findById(devolucionId)
                .orElseThrow(() -> new DevolucionNoEncontradaException(devolucionId));
    }

    @Override
    public List<Devolucion> consultarTodas() {
        return loadDevolucionPort.findAll();
    }

    @Override
    public List<Devolucion> consultarPorPedido(String pedidoId) {
        return loadDevolucionPort.findByPedidoId(pedidoId);
    }
}
