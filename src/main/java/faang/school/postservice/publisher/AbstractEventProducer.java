package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendEvent(T event, String topic) {
        log.info("Publishing an event {} - {}", topic, event);
        kafkaTemplate.send(topic, event);
    }
}