package com.grupo10.returns.infrastructure.adapter.out.persistence;

import com.grupo10.returns.domain.model.Devolucion;
import com.grupo10.returns.domain.model.EstadoDevolucion;
import com.grupo10.returns.domain.model.LineaDevolucion;
import com.grupo10.returns.infrastructure.adapter.out.persistence.entity.DevolucionJpaEntity;
import com.grupo10.returns.infrastructure.adapter.out.persistence.entity.LineaDevolucionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DevolucionPersistenceMapper {

    public Devolucion toDomain(DevolucionJpaEntity entity) {
        List<LineaDevolucion> lineas = entity.getLineas().stream()
                .map(l -> new LineaDevolucion(l.getLoteId(), l.getCantidadDevuelta(), l.getMotivoDevolucion()))
                .toList();
        return new Devolucion(
                entity.getId(),
                entity.getNumeroDevolucion(),
                entity.getPedidoId(),
                entity.getSolicitadoPor(),
                lineas,
                EstadoDevolucion.valueOf(entity.getEstado()),
                entity.getObservaciones(),
                entity.getMotivoRechazo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }

    public DevolucionJpaEntity toEntity(Devolucion domain) {
        var entity = new DevolucionJpaEntity();
        entity.setId(domain.getId());
        entity.setNumeroDevolucion(domain.getNumeroDevolucion());
        entity.setPedidoId(domain.getPedidoId());
        entity.setSolicitadoPor(domain.getSolicitadoPor());
        entity.setEstado(domain.getEstado().name());
        entity.setObservaciones(domain.getObservaciones());
        entity.setMotivoRechazo(domain.getMotivoRechazo());
        entity.setCreadoEn(domain.getCreadoEn());
        entity.setActualizadoEn(domain.getActualizadoEn());

        List<LineaDevolucionJpaEntity> lineas = domain.getLineas().stream()
                .map(l -> {
                    var linea = new LineaDevolucionJpaEntity();
                    linea.setDevolucion(entity);
                    linea.setLoteId(l.loteId());
                    linea.setCantidadDevuelta(l.cantidadDevuelta());
                    linea.setMotivoDevolucion(l.motivoDevolucion());
                    return linea;
                })
                .toList();
        entity.setLineas(lineas);
        return entity;
    }
}
