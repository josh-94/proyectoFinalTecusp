package com.grupo10.inventory.domain.exception;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(String productoId) {
        super("Producto no encontrado: " + productoId);
    }
}
