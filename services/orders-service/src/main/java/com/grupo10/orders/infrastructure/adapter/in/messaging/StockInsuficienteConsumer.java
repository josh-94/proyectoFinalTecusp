package com.grupo10.orders.infrastructure.adapter.in.messaging;

import com.grupo10.orders.application.port.in.RechazarPedidoUseCase;
import com.grupo10.orders.domain.exception.PedidoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StockInsuficienteConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockInsuficienteConsumer.class);

    private final RechazarPedidoUseCase rechazarPedidoUseCase;

    public StockInsuficienteConsumer(RechazarPedidoUseCase rechazarPedidoUseCase) {
        this.rechazarPedidoUseCase = rechazarPedidoUseCase;
    }

    @KafkaListener(topics = "inventory.stock.insuficiente", groupId = "orders-service")
    public void onStockInsuficiente(Map<String, Object> payload) {
        String pedidoId = extraerCampo(payload, "pedidoId");
        String motivo   = extraerCampo(payload, "motivo");

        if (pedidoId == null) {
            log.warn("Evento inventory.stock.insuficiente sin pedidoId, ignorando");
            return;
        }

        log.info("Stock insuficiente para pedidoId={}, motivo={}", pedidoId, motivo);
        try {
            rechazarPedidoUseCase.rechazar(pedidoId,
                    motivo != null ? motivo : "Stock insuficiente");
        } catch (PedidoNoEncontradoException ex) {
            log.error("Pedido no encontrado al rechazar: {}", pedidoId);
        }
    }

    private String extraerCampo(Map<String, Object> payload, String campo) {
        Object data = payload.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object value = dataMap.get(campo);
            return value != null ? value.toString() : null;
        }
        return null;
    }
}
