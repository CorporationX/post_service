package faang.school.postservice.kafka.handler;

import faang.school.postservice.event.KafkaEvent;

public interface EventHandler<T extends KafkaEvent> {
    void handle(T event);
    Class<T> getEventType();
}
