package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractProducer<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    protected void sendMessage(String topic, T message) {
        log.info("Sending event to topic: {}", topic);
        kafkaTemplate.send(topic, message);
    }
}
