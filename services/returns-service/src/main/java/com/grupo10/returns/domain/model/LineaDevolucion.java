package com.grupo10.returns.domain.model;

public record LineaDevolucion(
        String loteId,
        int cantidadDevuelta,
        String motivoDevolucion
) {}
