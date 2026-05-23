package com.grupo10.inventory.infrastructure.adapter.out.persistence.entity;

import com.grupo10.inventory.domain.model.TipoMovimiento;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "movimientos_stock")
public class MovimientoStockJpaEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteJpaEntity lote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "referencia_externa", length = 100)
    private String referenciaExterna;

    @Column(name = "creado_por", nullable = false, length = 100)
    private String creadoPor;

    protected MovimientoStockJpaEntity() {}

    public MovimientoStockJpaEntity(String id, LoteJpaEntity lote, TipoMovimiento tipo,
                                    int cantidad, String referenciaExterna, String creadoPor) {
        this.id = id;
        this.lote = lote;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.creadoEn = Instant.now();
        this.referenciaExterna = referenciaExterna;
        this.creadoPor = creadoPor;
    }

    public String getId() { return id; }
    public LoteJpaEntity getLote() { return lote; }
    public TipoMovimiento getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public Instant getCreadoEn() { return creadoEn; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public String getCreadoPor() { return creadoPor; }
}
