package com.grupo10.returns.application.service;

import com.grupo10.returns.application.port.in.RegistrarDevolucionUseCase;
import com.grupo10.returns.application.port.out.SaveDevolucionPort;
import com.grupo10.returns.domain.model.Devolucion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrarDevolucionService implements RegistrarDevolucionUseCase {

    private final SaveDevolucionPort saveDevolucionPort;

    public RegistrarDevolucionService(SaveDevolucionPort saveDevolucionPort) {
        this.saveDevolucionPort = saveDevolucionPort;
    }

    @Override
    public DevolucionRegistrada registrar(RegistrarDevolucionCommand command) {
        Devolucion devolucion = Devolucion.registrar(
                command.pedidoId(),
                command.solicitadoPor(),
                command.lineas()
        );
        Devolucion saved = saveDevolucionPort.save(devolucion);
        return new DevolucionRegistrada(saved.getId(), saved.getNumeroDevolucion());
    }
}
