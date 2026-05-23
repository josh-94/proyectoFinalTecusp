package com.grupo10.inventory.domain.exception;

public class LoteNoEncontradoException extends RuntimeException {

    public LoteNoEncontradoException(String loteId) {
        super("Lote no encontrado: " + loteId);
    }
}
