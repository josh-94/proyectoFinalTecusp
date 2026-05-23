package com.grupo10.inventory.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class ProductoJpaEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    protected ProductoJpaEntity() {}

    public ProductoJpaEntity(String id, String sku, String nombre, String descripcion,
                              String unidadMedida, int stockMinimo) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
    }

    public String getId() { return id; }
    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getUnidadMedida() { return unidadMedida; }
    public int getStockMinimo() { return stockMinimo; }
}
