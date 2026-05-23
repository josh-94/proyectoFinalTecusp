package com.grupo10.returns.infrastructure.adapter.out.persistence;

import com.grupo10.returns.application.port.out.LoadDevolucionPort;
import com.grupo10.returns.application.port.out.SaveDevolucionPort;
import com.grupo10.returns.domain.model.Devolucion;
import com.grupo10.returns.infrastructure.adapter.out.persistence.repository.DevolucionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReturnsPersistenceAdapter implements LoadDevolucionPort, SaveDevolucionPort {

    private final DevolucionJpaRepository devolucionJpaRepository;
    private final DevolucionPersistenceMapper mapper;

    public ReturnsPersistenceAdapter(DevolucionJpaRepository devolucionJpaRepository,
                                      DevolucionPersistenceMapper mapper) {
        this.devolucionJpaRepository = devolucionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Devolucion> findById(String devolucionId) {
        return devolucionJpaRepository.findById(devolucionId).map(mapper::toDomain);
    }

    @Override
    public List<Devolucion> findAll() {
        return devolucionJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Devolucion> findByPedidoId(String pedidoId) {
        return devolucionJpaRepository.findByPedidoId(pedidoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Devolucion save(Devolucion devolucion) {
        var saved = devolucionJpaRepository.save(mapper.toEntity(devolucion));
        return mapper.toDomain(saved);
    }
}
