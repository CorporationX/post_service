package faang.school.postservice.publisher.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class AbstractProducer<T> {
    protected final KafkaTemplate<String, Object> kafkaTemplate;

    public abstract void publish(T event);
}
