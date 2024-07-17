package faang.school.postservice.consumer;

import faang.school.postservice.event.kafka.KafkaEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaConsumer<T extends KafkaEvent> {

    void consume(T event, Acknowledgment ack);
}
