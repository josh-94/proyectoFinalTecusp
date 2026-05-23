package com.grupo10.inventory.infrastructure.adapter.out.persistence;

import com.grupo10.inventory.domain.model.Lote;
import com.grupo10.inventory.domain.model.Producto;
import com.grupo10.inventory.infrastructure.adapter.out.persistence.entity.LoteJpaEntity;
import com.grupo10.inventory.infrastructure.adapter.out.persistence.entity.ProductoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StockPersistenceMapper {

    public Producto toDomain(ProductoJpaEntity entity) {
        return new Producto(
                entity.getId(),
                entity.getSku(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getUnidadMedida(),
                entity.getStockMinimo()
        );
    }

    public Lote toDomain(LoteJpaEntity entity) {
        return new Lote(
                entity.getId(),
                entity.getProducto().getId(),
                entity.getNumeroLote(),
                entity.getFechaVencimiento(),
                entity.getCantidadDisponible()
        );
    }
}
