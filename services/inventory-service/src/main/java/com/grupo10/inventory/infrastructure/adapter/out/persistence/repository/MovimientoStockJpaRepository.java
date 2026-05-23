package com.grupo10.inventory.infrastructure.adapter.out.persistence.repository;

import com.grupo10.inventory.infrastructure.adapter.out.persistence.entity.MovimientoStockJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoStockJpaRepository extends JpaRepository<MovimientoStockJpaEntity, String> {}
