package com.grupo10.inventory.domain.model;

import com.grupo10.inventory.domain.exception.StockInsuficienteException;

import java.time.LocalDate;

public class Lote {

    private final String id;
    private final String productoId;
    private final String numeroLote;
    private final LocalDate fechaVencimiento;
    private int cantidadDisponible;

    public Lote(String id, String productoId, String numeroLote,
                LocalDate fechaVencimiento, int cantidadDisponible) {
        this.id = id;
        this.productoId = productoId;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidadDisponible = cantidadDisponible;
    }

    public void descontar(int cantidad) {
        if (this.cantidadDisponible < cantidad) {
            throw new StockInsuficienteException(this.id, this.cantidadDisponible, cantidad);
        }
        this.cantidadDisponible -= cantidad;
    }

    public void agregar(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser positiva");
        }
        this.cantidadDisponible += cantidad;
    }

    public boolean estaVencido() {
        return LocalDate.now().isAfter(this.fechaVencimiento);
    }

    public String getId() { return id; }
    public String getProductoId() { return productoId; }
    public String getNumeroLote() { return numeroLote; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public int getCantidadDisponible() { return cantidadDisponible; }
}
