package faang.school.postservice.outbox.publisher;

import faang.school.postservice.outbox.entity.OutboxEventType;

public interface OutboxEventPublisher {

    OutboxEventType getType();

    void publish(String payload);
}