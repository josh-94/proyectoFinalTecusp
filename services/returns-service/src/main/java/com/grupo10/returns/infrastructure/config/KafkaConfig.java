package com.grupo10.returns.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topicDevolucionAprobada() {
        return TopicBuilder.name("returns.devolucion.aprobada").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicDevolucionRechazada() {
        return TopicBuilder.name("returns.devolucion.rechazada").partitions(3).replicas(1).build();
    }
}
