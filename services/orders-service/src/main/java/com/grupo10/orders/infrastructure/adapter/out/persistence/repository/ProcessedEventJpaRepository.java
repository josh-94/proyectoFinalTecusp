package com.grupo10.orders.infrastructure.adapter.out.persistence.repository;

import com.grupo10.orders.infrastructure.adapter.out.persistence.entity.ProcessedEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventJpaEntity, String> {
}
