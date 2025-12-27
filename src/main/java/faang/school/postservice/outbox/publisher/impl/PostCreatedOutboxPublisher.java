package faang.school.postservice.outbox.publisher.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.messages.kafka.producers.PostCreatedProducer;
import faang.school.postservice.outbox.entity.OutboxEventType;
import faang.school.postservice.outbox.publisher.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCreatedOutboxPublisher implements OutboxEventPublisher {

    private final PostCreatedProducer postPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public OutboxEventType getType() {
        return OutboxEventType.POST_CREATED;
    }

    @Override
    public void publish(String payload) {
        try {
            PostCreatedEvent event = objectMapper.readValue(payload, PostCreatedEvent.class);
            postPublisher.publish(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse PostCreatedEvent payload", e);
        }
    }
}