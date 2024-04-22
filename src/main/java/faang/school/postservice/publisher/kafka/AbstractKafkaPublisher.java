package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class AbstractKafkaPublisher<Event> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send (String channelTopic, KafkaKey kafkaKey, Event event) {
        kafkaTemplate.send(channelTopic, kafkaKey.name(), event);
    }

    public void send (String channelTopic, Event event) {
        kafkaTemplate.send(channelTopic, event);
    }
}
