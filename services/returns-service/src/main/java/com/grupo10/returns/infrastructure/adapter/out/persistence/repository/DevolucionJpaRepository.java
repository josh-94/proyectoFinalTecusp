package com.grupo10.returns.infrastructure.adapter.out.persistence.repository;

import com.grupo10.returns.infrastructure.adapter.out.persistence.entity.DevolucionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevolucionJpaRepository extends JpaRepository<DevolucionJpaEntity, String> {
    List<DevolucionJpaEntity> findByPedidoId(String pedidoId);
}
