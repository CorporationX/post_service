package faang.school.postservice.producer;

import faang.school.postservice.event.kafka.KafkaEvent;

public interface KafkaProducer<T extends KafkaEvent> {
    void produce(T event);
}
