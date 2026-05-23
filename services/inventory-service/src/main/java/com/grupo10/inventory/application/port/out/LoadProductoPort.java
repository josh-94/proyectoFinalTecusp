package com.grupo10.inventory.application.port.out;

import com.grupo10.inventory.domain.model.Producto;

import java.util.Optional;

public interface LoadProductoPort {

    Optional<Producto> findProductoById(String id);
}
