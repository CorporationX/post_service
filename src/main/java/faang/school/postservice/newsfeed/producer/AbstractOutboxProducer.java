package faang.school.postservice.newsfeed.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.outbox.AggregateType;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.model.outbox.OutboxFeedEvent;
import faang.school.postservice.repository.outbox.OutboxRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AbstractOutboxProducer<T> {

    protected OutboxRepository outboxRepository;
    protected ObjectMapper objectMapper;

    public AbstractOutboxProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void saveToOutbox(AggregateType aggregateType, Long aggregateId, EventType eventType, T eventPayload) {
        try {
            String payload = objectMapper.writeValueAsString(eventPayload);
            OutboxFeedEvent event = OutboxFeedEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payload)
                    .processed(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
