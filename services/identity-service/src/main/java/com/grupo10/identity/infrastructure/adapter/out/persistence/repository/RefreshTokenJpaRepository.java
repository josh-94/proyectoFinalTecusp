package com.grupo10.identity.infrastructure.adapter.out.persistence.repository;

import com.grupo10.identity.infrastructure.adapter.out.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, String> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);
}
