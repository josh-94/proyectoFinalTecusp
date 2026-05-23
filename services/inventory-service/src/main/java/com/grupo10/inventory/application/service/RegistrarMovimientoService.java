package com.grupo10.inventory.application.service;

import com.grupo10.inventory.application.port.in.RegistrarMovimientoUseCase;
import com.grupo10.inventory.application.port.out.LoadLotePort;
import com.grupo10.inventory.application.port.out.PublishDomainEventPort;
import com.grupo10.inventory.application.port.out.SaveMovimientoPort;
import com.grupo10.inventory.domain.event.StockMovimientoRegistradoEvent;
import com.grupo10.inventory.domain.exception.LoteNoEncontradoException;
import com.grupo10.inventory.domain.model.Lote;
import com.grupo10.inventory.domain.model.TipoMovimiento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrarMovimientoService implements RegistrarMovimientoUseCase {

    private final LoadLotePort loadLotePort;
    private final SaveMovimientoPort saveMovimientoPort;
    private final PublishDomainEventPort publishEventPort;

    public RegistrarMovimientoService(LoadLotePort loadLotePort,
                                      SaveMovimientoPort saveMovimientoPort,
                                      PublishDomainEventPort publishEventPort) {
        this.loadLotePort = loadLotePort;
        this.saveMovimientoPort = saveMovimientoPort;
        this.publishEventPort = publishEventPort;
    }

    @Override
    public MovimientoResult registrar(RegistrarMovimientoCommand command) {
        Lote lote = loadLotePort.findLoteById(command.loteId())
                .orElseThrow(() -> new LoteNoEncontradoException(command.loteId()));

        aplicarMovimiento(lote, command.tipo(), command.cantidad());

        String movimientoId = saveMovimientoPort.saveMovimiento(
                lote,
                command.tipo(),
                command.cantidad(),
                command.referenciaExterna(),
                command.creadoPor());

        saveMovimientoPort.updateLote(lote);

        publishEventPort.publish(StockMovimientoRegistradoEvent.of(
                lote.getId(),
                lote.getProductoId(),
                command.tipo(),
                command.cantidad(),
                command.referenciaExterna()));

        return new MovimientoResult(movimientoId, lote.getId(), lote.getCantidadDisponible());
    }

    private void aplicarMovimiento(Lote lote, TipoMovimiento tipo, int cantidad) {
        switch (tipo) {
            case ENTRADA, LIBERACION -> lote.agregar(cantidad);
            case SALIDA, RESERVA     -> lote.descontar(cantidad);
        }
    }
}
