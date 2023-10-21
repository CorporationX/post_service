package faang.school.postservice.messaging.kafka.publishing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractProducer<T>  {
    protected final KafkaTemplate<String, Object> kafkaTemplate;

    public abstract void publish(T event);
}
