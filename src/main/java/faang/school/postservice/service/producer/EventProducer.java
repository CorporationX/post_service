package faang.school.postservice.service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class EventProducer<T> {
    private final KafkaTemplate<String, Object> template;
    private final NewTopic topic;

    public void sendEvent(T event) {
        template.send(topic.name(), event);
    }
}
