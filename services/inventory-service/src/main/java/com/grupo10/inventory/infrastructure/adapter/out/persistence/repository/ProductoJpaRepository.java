package com.grupo10.inventory.infrastructure.adapter.out.persistence.repository;

import com.grupo10.inventory.infrastructure.adapter.out.persistence.entity.ProductoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoJpaRepository extends JpaRepository<ProductoJpaEntity, String> {}
