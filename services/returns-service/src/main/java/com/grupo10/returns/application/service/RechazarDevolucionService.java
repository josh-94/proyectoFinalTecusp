package com.grupo10.returns.application.service;

import com.grupo10.returns.application.port.in.RechazarDevolucionUseCase;
import com.grupo10.returns.application.port.out.LoadDevolucionPort;
import com.grupo10.returns.application.port.out.SaveDevolucionPort;
import com.grupo10.returns.domain.exception.DevolucionNoEncontradaException;
import com.grupo10.returns.domain.model.Devolucion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RechazarDevolucionService implements RechazarDevolucionUseCase {

    private final LoadDevolucionPort loadDevolucionPort;
    private final SaveDevolucionPort saveDevolucionPort;

    public RechazarDevolucionService(LoadDevolucionPort loadDevolucionPort,
                                      SaveDevolucionPort saveDevolucionPort) {
        this.loadDevolucionPort = loadDevolucionPort;
        this.saveDevolucionPort = saveDevolucionPort;
    }

    @Override
    public void rechazar(String devolucionId, String motivo) {
        Devolucion devolucion = loadDevolucionPort.findById(devolucionId)
                .orElseThrow(() -> new DevolucionNoEncontradaException(devolucionId));
        devolucion.rechazar(motivo);
        saveDevolucionPort.save(devolucion);
    }
}
