package com.grupo10.orders.infrastructure.adapter.out.persistence;

import com.grupo10.orders.application.port.out.LoadPedidoPort;
import com.grupo10.orders.application.port.out.SavePedidoPort;
import com.grupo10.orders.domain.model.Pedido;
import com.grupo10.orders.infrastructure.adapter.out.persistence.repository.PedidoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrdersPersistenceAdapter implements LoadPedidoPort, SavePedidoPort {

    private final PedidoJpaRepository pedidoJpaRepository;
    private final PedidoPersistenceMapper mapper;

    public OrdersPersistenceAdapter(PedidoJpaRepository pedidoJpaRepository,
                                     PedidoPersistenceMapper mapper) {
        this.pedidoJpaRepository = pedidoJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Pedido> findById(String pedidoId) {
        return pedidoJpaRepository.findById(pedidoId).map(mapper::toDomain);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Pedido> findBySolicitadoPor(String solicitadoPor) {
        return pedidoJpaRepository.findBySolicitadoPor(solicitadoPor).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Pedido save(Pedido pedido) {
        var saved = pedidoJpaRepository.save(mapper.toEntity(pedido));
        return mapper.toDomain(saved);
    }
}
