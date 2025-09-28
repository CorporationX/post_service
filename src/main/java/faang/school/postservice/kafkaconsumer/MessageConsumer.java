package faang.school.postservice.kafkaconsumer;

import org.springframework.kafka.support.Acknowledgment;

public interface MessageConsumer<T> {
    void consume(T event, Acknowledgment acknowledgment);
}
