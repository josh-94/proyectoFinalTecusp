package com.grupo10.inventory.infrastructure.adapter.out.messaging;

import com.grupo10.inventory.application.port.out.PublishDomainEventPort;
import com.grupo10.inventory.domain.event.StockMovimientoRegistradoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaEventPublisher implements PublishDomainEventPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private static final String TOPIC = "inventory.stock.movimiento.registrado";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(StockMovimientoRegistradoEvent event) {
        var envelope = Map.of(
                "eventId",     event.eventId(),
                "eventType",   "inventory.stock.movimiento.registrado",
                "occurredAt",  event.occurredAt().toString(),
                "producer",    "inventory-service",
                "version",     1,
                "data", Map.of(
                        "loteId",            event.loteId(),
                        "productoId",        event.productoId(),
                        "tipo",              event.tipo().name(),
                        "cantidad",          event.cantidad(),
                        "referenciaExterna", event.referenciaExterna() != null ? event.referenciaExterna() : ""
                )
        );
        kafkaTemplate.send(TOPIC, event.loteId(), envelope);
        log.info("Evento publicado: topic={}, eventId={}", TOPIC, event.eventId());
    }
}
