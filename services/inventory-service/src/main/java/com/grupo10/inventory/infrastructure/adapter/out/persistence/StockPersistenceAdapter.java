package com.grupo10.inventory.infrastructure.adapter.out.persistence;

import com.grupo10.inventory.application.port.out.LoadLotePort;
import com.grupo10.inventory.application.port.out.LoadProductoPort;
import com.grupo10.inventory.application.port.out.SaveMovimientoPort;
import com.grupo10.inventory.domain.model.Lote;
import com.grupo10.inventory.domain.model.Producto;
import com.grupo10.inventory.domain.model.TipoMovimiento;
import com.grupo10.inventory.infrastructure.adapter.out.persistence.entity.MovimientoStockJpaEntity;
import com.grupo10.inventory.infrastructure.adapter.out.persistence.repository.LoteJpaRepository;
import com.grupo10.inventory.infrastructure.adapter.out.persistence.repository.MovimientoStockJpaRepository;
import com.grupo10.inventory.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StockPersistenceAdapter implements LoadProductoPort, LoadLotePort, SaveMovimientoPort {

    private final ProductoJpaRepository productoRepo;
    private final LoteJpaRepository loteRepo;
    private final MovimientoStockJpaRepository movimientoRepo;
    private final StockPersistenceMapper mapper;

    public StockPersistenceAdapter(ProductoJpaRepository productoRepo,
                                   LoteJpaRepository loteRepo,
                                   MovimientoStockJpaRepository movimientoRepo,
                                   StockPersistenceMapper mapper) {
        this.productoRepo = productoRepo;
        this.loteRepo = loteRepo;
        this.movimientoRepo = movimientoRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Producto> findProductoById(String id) {
        return productoRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Lote> findLoteById(String id) {
        return loteRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Lote> findLotesByProductoId(String productoId) {
        return loteRepo.findByProductoId(productoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Lote> findAllLotes() {
        return loteRepo.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public String saveMovimiento(Lote lote, TipoMovimiento tipo, int cantidad,
                                 String referenciaExterna, String creadoPor) {
        var loteEntity = loteRepo.findById(lote.getId()).orElseThrow();
        String movimientoId = UUID.randomUUID().toString();
        movimientoRepo.save(new MovimientoStockJpaEntity(
                movimientoId, loteEntity, tipo, cantidad, referenciaExterna, creadoPor));
        return movimientoId;
    }

    @Override
    public void updateLote(Lote lote) {
        loteRepo.findById(lote.getId()).ifPresent(entity -> {
            entity.setCantidadDisponible(lote.getCantidadDisponible());
            loteRepo.save(entity);
        });
    }
}
