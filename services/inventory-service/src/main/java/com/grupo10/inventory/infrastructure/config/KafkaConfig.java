package com.grupo10.inventory.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic inventoryStockMovimientoRegistrado() {
        return TopicBuilder.name("inventory.stock.movimiento.registrado")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryStockReservado() {
        return TopicBuilder.name("inventory.stock.reservado")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryStockInsuficiente() {
        return TopicBuilder.name("inventory.stock.insuficiente")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryAlertaCaducidad() {
        return TopicBuilder.name("inventory.alerta.caducidad")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryAlertaStockMinimo() {
        return TopicBuilder.name("inventory.alerta.stock-minimo")
                .partitions(3).replicas(1).build();
    }
}
