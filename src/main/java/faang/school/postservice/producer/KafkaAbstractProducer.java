package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class KafkaAbstractProducer<V> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    protected void sendMessage(String topicName, V message) {
        kafkaTemplate.send(topicName, message);
    }
}
