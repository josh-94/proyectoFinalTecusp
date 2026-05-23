package com.grupo10.inventory.domain.model;

public class Producto {

    private final String id;
    private final String sku;
    private final String nombre;
    private final String descripcion;
    private final String unidadMedida;
    private final int stockMinimo;

    public Producto(String id, String sku, String nombre, String descripcion,
                    String unidadMedida, int stockMinimo) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
    }

    public boolean stockBajoMinimo(int cantidadActual) {
        return cantidadActual < stockMinimo;
    }

    public String getId() { return id; }
    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getUnidadMedida() { return unidadMedida; }
    public int getStockMinimo() { return stockMinimo; }
}
