package com.grupo10.orders.infrastructure.adapter.out.messaging;

import com.grupo10.orders.application.port.out.PublishPedidoEventPort;
import com.grupo10.orders.domain.event.PedidoCreadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPedidoEventPublisher implements PublishPedidoEventPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaPedidoEventPublisher.class);
    private static final String TOPIC = "orders.pedido.creado";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPedidoEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(PedidoCreadoEvent event) {
        kafkaTemplate.send(TOPIC, event.data().pedidoId(), event);
        log.info("Evento publicado: topic={}, pedidoId={}", TOPIC, event.data().pedidoId());
    }
}
