package com.grupo10.orders.infrastructure.adapter.out.persistence;

import com.grupo10.orders.domain.model.EstadoPedido;
import com.grupo10.orders.domain.model.LineaDePedido;
import com.grupo10.orders.domain.model.Pedido;
import com.grupo10.orders.infrastructure.adapter.out.persistence.entity.LineaDePedidoJpaEntity;
import com.grupo10.orders.infrastructure.adapter.out.persistence.entity.PedidoJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoPersistenceMapper {

    public Pedido toDomain(PedidoJpaEntity entity) {
        List<LineaDePedido> lineas = entity.getLineas().stream()
                .map(l -> new LineaDePedido(l.getLoteId(), l.getCantidad(), l.getDescripcion()))
                .toList();
        return new Pedido(
                entity.getId(),
                entity.getNumeroPedido(),
                entity.getSolicitadoPor(),
                entity.getHospitalDestino(),
                lineas,
                EstadoPedido.valueOf(entity.getEstado()),
                entity.getMotivoRechazo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }

    public PedidoJpaEntity toEntity(Pedido domain) {
        var entity = new PedidoJpaEntity();
        entity.setId(domain.getId());
        entity.setNumeroPedido(domain.getNumeroPedido());
        entity.setSolicitadoPor(domain.getSolicitadoPor());
        entity.setHospitalDestino(domain.getHospitalDestino());
        entity.setEstado(domain.getEstado().name());
        entity.setMotivoRechazo(domain.getMotivoRechazo());
        entity.setCreadoEn(domain.getCreadoEn());
        entity.setActualizadoEn(domain.getActualizadoEn());

        List<LineaDePedidoJpaEntity> lineas = domain.getLineas().stream()
                .map(l -> {
                    var linea = new LineaDePedidoJpaEntity();
                    linea.setPedido(entity);
                    linea.setLoteId(l.loteId());
                    linea.setCantidad(l.cantidad());
                    linea.setDescripcion(l.descripcion());
                    return linea;
                })
                .toList();
        entity.setLineas(lineas);
        return entity;
    }
}
