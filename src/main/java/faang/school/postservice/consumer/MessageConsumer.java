package faang.school.postservice.consumer;

import org.springframework.kafka.support.Acknowledgment;

public interface MessageConsumer<T> {
    void consume(T event, Acknowledgment ack);
}
