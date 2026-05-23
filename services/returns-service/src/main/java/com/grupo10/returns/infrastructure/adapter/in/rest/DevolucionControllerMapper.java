package com.grupo10.returns.infrastructure.adapter.in.rest;

import com.grupo10.returns.domain.model.Devolucion;
import com.grupo10.returns.domain.model.LineaDevolucion;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.DevolucionResponse;
import com.grupo10.returns.infrastructure.adapter.in.rest.dto.RegistrarDevolucionRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DevolucionControllerMapper {

    public List<LineaDevolucion> toLineas(
            List<RegistrarDevolucionRequest.LineaDevolucionRequest> requests) {
        return requests.stream()
                .map(r -> new LineaDevolucion(r.loteId(), r.cantidadDevuelta(), r.motivoDevolucion()))
                .toList();
    }

    public DevolucionResponse toResponse(Devolucion devolucion) {
        var lineas = devolucion.getLineas().stream()
                .map(l -> new DevolucionResponse.LineaResponse(
                        l.loteId(), l.cantidadDevuelta(), l.motivoDevolucion()))
                .toList();
        return new DevolucionResponse(
                devolucion.getId(),
                devolucion.getNumeroDevolucion(),
                devolucion.getPedidoId(),
                devolucion.getSolicitadoPor(),
                devolucion.getEstado().name(),
                devolucion.getObservaciones(),
                devolucion.getMotivoRechazo(),
                lineas,
                devolucion.getCreadoEn(),
                devolucion.getActualizadoEn()
        );
    }
}
