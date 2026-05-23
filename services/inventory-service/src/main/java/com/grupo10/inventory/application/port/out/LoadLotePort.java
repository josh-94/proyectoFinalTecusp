package com.grupo10.inventory.application.port.out;

import com.grupo10.inventory.domain.model.Lote;

import java.util.List;
import java.util.Optional;

public interface LoadLotePort {

    Optional<Lote> findLoteById(String id);

    List<Lote> findLotesByProductoId(String productoId);

    List<Lote> findAllLotes();
}
