package com.grupo10.orders.infrastructure.adapter.out.persistence.repository;

import com.grupo10.orders.infrastructure.adapter.out.persistence.entity.PedidoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, String> {
    List<PedidoJpaEntity> findBySolicitadoPor(String solicitadoPor);
}
