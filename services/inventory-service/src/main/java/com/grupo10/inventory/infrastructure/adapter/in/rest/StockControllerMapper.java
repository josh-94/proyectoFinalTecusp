package com.grupo10.inventory.infrastructure.adapter.in.rest;

import com.grupo10.inventory.domain.model.Lote;
import com.grupo10.inventory.infrastructure.adapter.in.rest.dto.StockResponse;
import org.springframework.stereotype.Component;

@Component
public class StockControllerMapper {

    public StockResponse toResponse(Lote lote) {
        return new StockResponse(
                lote.getId(),
                lote.getProductoId(),
                lote.getNumeroLote(),
                lote.getFechaVencimiento(),
                lote.getCantidadDisponible(),
                lote.estaVencido()
        );
    }
}
