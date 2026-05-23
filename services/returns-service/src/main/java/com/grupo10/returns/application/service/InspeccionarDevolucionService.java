package com.grupo10.returns.application.service;

import com.grupo10.returns.application.port.in.InspeccionarDevolucionUseCase;
import com.grupo10.returns.application.port.out.LoadDevolucionPort;
import com.grupo10.returns.application.port.out.SaveDevolucionPort;
import com.grupo10.returns.domain.exception.DevolucionNoEncontradaException;
import com.grupo10.returns.domain.model.Devolucion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InspeccionarDevolucionService implements InspeccionarDevolucionUseCase {

    private final LoadDevolucionPort loadDevolucionPort;
    private final SaveDevolucionPort saveDevolucionPort;

    public InspeccionarDevolucionService(LoadDevolucionPort loadDevolucionPort,
                                          SaveDevolucionPort saveDevolucionPort) {
        this.loadDevolucionPort = loadDevolucionPort;
        this.saveDevolucionPort = saveDevolucionPort;
    }

    @Override
    public void inspeccionar(String devolucionId, String observaciones) {
        Devolucion devolucion = loadDevolucionPort.findById(devolucionId)
                .orElseThrow(() -> new DevolucionNoEncontradaException(devolucionId));
        devolucion.inspeccionar(observaciones);
        saveDevolucionPort.save(devolucion);
    }
}
