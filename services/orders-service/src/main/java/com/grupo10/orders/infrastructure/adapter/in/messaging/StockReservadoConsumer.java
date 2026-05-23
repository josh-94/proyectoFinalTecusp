package com.grupo10.orders.infrastructure.adapter.in.messaging;

import com.grupo10.orders.application.port.in.ConfirmarPedidoUseCase;
import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StockReservadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockReservadoConsumer.class);

    private final ConfirmarPedidoUseCase confirmarPedidoUseCase;

    public StockReservadoConsumer(ConfirmarPedidoUseCase confirmarPedidoUseCase) {
        this.confirmarPedidoUseCase = confirmarPedidoUseCase;
    }

    @KafkaListener(topics = "inventory.stock.reservado", groupId = "orders-service")
    public void onStockReservado(Map<String, Object> payload) {
        String pedidoId = extraerPedidoId(payload);
        if (pedidoId == null) {
            log.warn("Evento inventory.stock.reservado sin pedidoId, ignorando");
            return;
        }

        log.info("Stock reservado para pedidoId={}", pedidoId);
        try {
            confirmarPedidoUseCase.confirmar(pedidoId);
        } catch (PedidoNoEncontradoException ex) {
            log.error("Pedido no encontrado al confirmar: {}", pedidoId);
        }
    }

    private String extraerPedidoId(Map<String, Object> payload) {
        Object data = payload.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object value = dataMap.get("pedidoId");
            return value != null ? value.toString() : null;
        }
        return null;
    }
}
