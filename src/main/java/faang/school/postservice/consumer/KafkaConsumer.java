package faang.school.postservice.consumer;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaConsumer<T> {
    void onMessage(T event, Acknowledgment acknowledgment);
}
