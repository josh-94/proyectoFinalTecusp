package com.grupo10.inventory.infrastructure.adapter.out.persistence.repository;

import com.grupo10.inventory.infrastructure.adapter.out.persistence.entity.LoteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoteJpaRepository extends JpaRepository<LoteJpaEntity, String> {

    List<LoteJpaEntity> findByProductoId(String productoId);
}
