package com.grupo10.orders.infrastructure.adapter.in.rest;

import com.grupo10.orders.domain.model.LineaDePedido;
import com.grupo10.orders.domain.model.Pedido;
import com.grupo10.orders.infrastructure.adapter.in.rest.dto.CrearPedidoRequest;
import com.grupo10.orders.infrastructure.adapter.in.rest.dto.PedidoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoControllerMapper {

    public List<LineaDePedido> toLineas(List<CrearPedidoRequest.LineaDePedidoRequest> requests) {
        return requests.stream()
                .map(r -> new LineaDePedido(r.loteId(), r.cantidad(), r.descripcion()))
                .toList();
    }

    public PedidoResponse toResponse(Pedido pedido) {
        var lineas = pedido.getLineas().stream()
                .map(l -> new PedidoResponse.LineaResponse(l.loteId(), l.cantidad(), l.descripcion()))
                .toList();
        return new PedidoResponse(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getSolicitadoPor(),
                pedido.getHospitalDestino(),
                pedido.getEstado().name(),
                pedido.getMotivoRechazo(),
                lineas,
                pedido.getCreadoEn(),
                pedido.getActualizadoEn()
        );
    }
}
