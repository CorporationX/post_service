package faang.school.postservice.messaging.listener.kafka;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaEventListener<T> {
    public void onMessage(T event, Acknowledgment acknowledgment);
}
