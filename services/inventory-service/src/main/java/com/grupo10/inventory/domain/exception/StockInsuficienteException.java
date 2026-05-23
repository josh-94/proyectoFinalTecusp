package com.grupo10.inventory.domain.exception;

public class StockInsuficienteException extends RuntimeException {

    private final int disponible;
    private final int solicitado;

    public StockInsuficienteException(String loteId, int disponible, int solicitado) {
        super(String.format("Stock insuficiente en lote '%s': disponible=%d, solicitado=%d",
                loteId, disponible, solicitado));
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public int getDisponible() { return disponible; }
    public int getSolicitado() { return solicitado; }
}
