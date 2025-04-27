package faang.school.postservice.publisher.kafka;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
public abstract class AbstractEventProducer<T> {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private NewTopic topic;

    public void sendEvent(T event) {
        log.info("Отправка ивента {} в Kafka с топиком {}", event, topic.name());
        kafkaTemplate.send(topic.name(), event);
    }
}
