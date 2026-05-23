package com.grupo10.inventory.application.port.in;

import com.grupo10.inventory.domain.model.Lote;

import java.util.List;

public interface ConsultarStockUseCase {

    List<Lote> listarTodos();

    List<Lote> consultarPorProducto(String productoId);

    Lote consultarLote(String loteId);
}
