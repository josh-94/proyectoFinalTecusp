package com.grupo10.orders.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "processed_events")
public class ProcessedEventJpaEntity {

    @Id
    private String eventId;

    @Column(nullable = false)
    private String topic;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public ProcessedEventJpaEntity() {}

    public ProcessedEventJpaEntity(String eventId, String topic, Instant processedAt) {
        this.eventId = eventId;
        this.topic = topic;
        this.processedAt = processedAt;
    }

    public String getEventId()           { return eventId; }
    public String getTopic()             { return topic; }
    public Instant getProcessedAt()      { return processedAt; }
}
