package com.grupo10.orders.domain.model;

public record LineaDePedido(
        String loteId,
        int cantidad,
        String descripcion
) {}
