package com.grupo10.returns.infrastructure.adapter.out.messaging;

import com.grupo10.returns.application.port.out.PublishDevolucionEventPort;
import com.grupo10.returns.domain.event.DevolucionAprobadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDevolucionEventPublisher implements PublishDevolucionEventPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaDevolucionEventPublisher.class);
    private static final String TOPIC = "returns.devolucion.aprobada";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDevolucionEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(DevolucionAprobadaEvent event) {
        kafkaTemplate.send(TOPIC, event.data().devolucionId(), event);
        log.info("Evento publicado: topic={}, devolucionId={}", TOPIC, event.data().devolucionId());
    }
}
