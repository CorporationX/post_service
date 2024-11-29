package faang.school.postservice.listener;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaEventListener<T> {

    void onMessage(T t, Acknowledgment acknowledgment);
}
