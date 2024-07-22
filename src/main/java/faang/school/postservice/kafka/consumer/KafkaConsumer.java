package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.KafkaEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaConsumer<T extends KafkaEvent> {

    void consume(T event, Acknowledgment ack);
}
