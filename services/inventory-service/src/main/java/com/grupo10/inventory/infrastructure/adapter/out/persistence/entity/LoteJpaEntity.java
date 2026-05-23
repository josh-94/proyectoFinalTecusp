package com.grupo10.inventory.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "lotes")
public class LoteJpaEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoJpaEntity producto;

    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_disponible", nullable = false)
    private int cantidadDisponible;

    protected LoteJpaEntity() {}

    public LoteJpaEntity(String id, ProductoJpaEntity producto, String numeroLote,
                         LocalDate fechaVencimiento, int cantidadDisponible) {
        this.id = id;
        this.producto = producto;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getId() { return id; }
    public ProductoJpaEntity getProducto() { return producto; }
    public String getNumeroLote() { return numeroLote; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
}
