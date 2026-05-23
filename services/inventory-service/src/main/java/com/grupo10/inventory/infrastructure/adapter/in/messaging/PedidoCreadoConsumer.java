package com.grupo10.inventory.infrastructure.adapter.in.messaging;

import com.grupo10.inventory.application.port.in.RegistrarMovimientoUseCase;
import com.grupo10.inventory.application.port.out.PublishDomainEventPort;
import com.grupo10.inventory.domain.exception.StockInsuficienteException;
import com.grupo10.inventory.domain.model.TipoMovimiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PedidoCreadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(PedidoCreadoConsumer.class);

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PedidoCreadoConsumer(RegistrarMovimientoUseCase registrarMovimientoUseCase,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.registrarMovimientoUseCase = registrarMovimientoUseCase;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "orders.pedido.creado", groupId = "inventory-service")
    public void onPedidoCreado(PedidoCreadoEvent event) {
        log.info("Procesando PedidoCreado: eventId={}, pedidoId={}",
                event.eventId(), event.data().pedidoId());

        try {
            for (ItemPedido item : event.data().items()) {
                var command = new RegistrarMovimientoUseCase.RegistrarMovimientoCommand(
                        item.loteId(),
                        TipoMovimiento.RESERVA,
                        item.cantidad(),
                        event.data().pedidoId(),
                        "orders-service"
                );
                registrarMovimientoUseCase.registrar(command);
            }
            publicarStockReservado(event.data().pedidoId());

        } catch (StockInsuficienteException ex) {
            log.warn("Stock insuficiente para pedido {}: {}", event.data().pedidoId(), ex.getMessage());
            publicarStockInsuficiente(event.data().pedidoId(), ex.getMessage());
        }
    }

    private void publicarStockReservado(String pedidoId) {
        var payload = Map.of(
                "eventType", "inventory.stock.reservado",
                "producer", "inventory-service",
                "version", 1,
                "data", Map.of("pedidoId", pedidoId)
        );
        kafkaTemplate.send("inventory.stock.reservado", pedidoId, payload);
    }

    private void publicarStockInsuficiente(String pedidoId, String motivo) {
        var payload = Map.of(
                "eventType", "inventory.stock.insuficiente",
                "producer", "inventory-service",
                "version", 1,
                "data", Map.of("pedidoId", pedidoId, "motivo", motivo)
        );
        kafkaTemplate.send("inventory.stock.insuficiente", pedidoId, payload);
    }

    // ── Envelope del evento entrante ──────────────────────────────────────

    public record PedidoCreadoEvent(
            String eventId,
            String eventType,
            String occurredAt,
            String producer,
            int version,
            PedidoData data
    ) {}

    public record PedidoData(
            String pedidoId,
            String solicitanteId,
            String area,
            List<ItemPedido> items
    ) {}

    public record ItemPedido(
            String loteId,
            int cantidad
    ) {}
}
