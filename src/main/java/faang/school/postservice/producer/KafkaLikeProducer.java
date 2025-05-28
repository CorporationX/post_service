package faang.school.postservice.producer;

import faang.school.postservice.event.LikeEventDto;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {

    private final GenericKafkaProducer<LikeEventDto> producer;
    private final KafkaProperties kafkaProperties;

    public void sendLikeEvent(LikeEventDto event) {
        String topic = kafkaProperties.getTopics().getLikes();
        producer.sendEvent(topic, event, "LikeEvent");
    }
}
