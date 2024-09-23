package faang.school.postservice.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@AllArgsConstructor
public abstract class AbstractProducer<E> {

    protected final KafkaTemplate<String, Object> kafkaTemplate;

    protected final String topicName;

    public void sendEvent(E event) {
        kafkaTemplate.send(topicName, event);
        log.info("Sent message to topic {}: {}", topicName, event.toString());
    }
}
