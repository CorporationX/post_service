package faang.school.postservice.publisher.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<Event> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send (String channelTopic, Event event) {
        kafkaTemplate.send(channelTopic, event);
    }
}
