package com.grupo10.inventory.application.service;

import com.grupo10.inventory.application.port.in.ConsultarStockUseCase;
import com.grupo10.inventory.application.port.out.LoadLotePort;
import com.grupo10.inventory.domain.exception.LoteNoEncontradoException;
import com.grupo10.inventory.domain.model.Lote;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultarStockService implements ConsultarStockUseCase {

    private final LoadLotePort loadLotePort;

    public ConsultarStockService(LoadLotePort loadLotePort) {
        this.loadLotePort = loadLotePort;
    }

    @Override
    public List<Lote> listarTodos() {
        return loadLotePort.findAllLotes();
    }

    @Override
    public List<Lote> consultarPorProducto(String productoId) {
        return loadLotePort.findLotesByProductoId(productoId);
    }

    @Override
    public Lote consultarLote(String loteId) {
        return loadLotePort.findLoteById(loteId)
                .orElseThrow(() -> new LoteNoEncontradoException(loteId));
    }
}
