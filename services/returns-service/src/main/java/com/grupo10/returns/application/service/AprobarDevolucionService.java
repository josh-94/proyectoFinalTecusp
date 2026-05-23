package com.grupo10.returns.application.service;

import com.grupo10.returns.application.port.in.AprobarDevolucionUseCase;
import com.grupo10.returns.application.port.out.LoadDevolucionPort;
import com.grupo10.returns.application.port.out.PublishDevolucionEventPort;
import com.grupo10.returns.application.port.out.SaveDevolucionPort;
import com.grupo10.returns.domain.event.DevolucionAprobadaEvent;
import com.grupo10.returns.domain.exception.DevolucionNoEncontradaException;
import com.grupo10.returns.domain.model.Devolucion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AprobarDevolucionService implements AprobarDevolucionUseCase {

    private final LoadDevolucionPort loadDevolucionPort;
    private final SaveDevolucionPort saveDevolucionPort;
    private final PublishDevolucionEventPort publishDevolucionEventPort;

    public AprobarDevolucionService(LoadDevolucionPort loadDevolucionPort,
                                     SaveDevolucionPort saveDevolucionPort,
                                     PublishDevolucionEventPort publishDevolucionEventPort) {
        this.loadDevolucionPort = loadDevolucionPort;
        this.saveDevolucionPort = saveDevolucionPort;
        this.publishDevolucionEventPort = publishDevolucionEventPort;
    }

    @Override
    public void aprobar(String devolucionId) {
        Devolucion devolucion = loadDevolucionPort.findById(devolucionId)
                .orElseThrow(() -> new DevolucionNoEncontradaException(devolucionId));
        devolucion.aprobar();
        saveDevolucionPort.save(devolucion);

        DevolucionAprobadaEvent event = DevolucionAprobadaEvent.of(
                devolucion.getId(),
                devolucion.getPedidoId(),
                devolucion.getLineas()
        );
        publishDevolucionEventPort.publish(event);
    }
}
