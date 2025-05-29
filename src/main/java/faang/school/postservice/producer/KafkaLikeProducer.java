package faang.school.postservice.producer;

import faang.school.postservice.event.LikeEventDto;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {

    private static final String EVENT_TYPE = "LikeEvent";

    private final GenericKafkaProducer<LikeEventDto> producer;
    private final KafkaProperties kafkaProperties;

    public void sendLikeEvent(LikeEventDto event) {
        if (kafkaProperties == null || kafkaProperties.getTopics() == null) {
            throw new IllegalStateException("Kafka properties are not configured properly");
        }

        String topic = kafkaProperties.getTopics().getLikes();
        if (topic == null || topic.isBlank()) {
            throw new IllegalStateException("Likes topic is not configured in Kafka properties");
        }

        producer.sendEvent(topic, event, EVENT_TYPE);
    }
}
