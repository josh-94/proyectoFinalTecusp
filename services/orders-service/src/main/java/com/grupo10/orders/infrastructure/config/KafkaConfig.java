package com.grupo10.orders.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topicPedidoCreado() {
        return TopicBuilder.name("orders.pedido.creado").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicPedidoCancelado() {
        return TopicBuilder.name("orders.pedido.cancelado").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicPedidoDespachado() {
        return TopicBuilder.name("orders.pedido.despachado").partitions(3).replicas(1).build();
    }
}
