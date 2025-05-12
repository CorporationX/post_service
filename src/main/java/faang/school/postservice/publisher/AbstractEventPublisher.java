package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topic;

    public void sendEvent(T event) {
        kafkaTemplate.send(topic.name(), event);
        log.info("JSON event: {} successfully send to Kafka topic: {}", event, topic.name());
    }
}
