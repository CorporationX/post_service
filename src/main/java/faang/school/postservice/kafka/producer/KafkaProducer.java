package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.KafkaEvent;

public interface KafkaProducer<T extends KafkaEvent> {
    void produce(T event);

    void produce(T event, Runnable runnable);
}
