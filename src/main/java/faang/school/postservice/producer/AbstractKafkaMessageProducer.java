package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaMessageProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topic;

    public void sendMessage(T message) {
        kafkaTemplate.send(topic.name(), message);
        log.debug("Message - {} sent in topic -{}", message, topic.name());
    }
}
