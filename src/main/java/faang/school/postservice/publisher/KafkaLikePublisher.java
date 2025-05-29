package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publish(LikeEvent event) {
        String topic = kafkaProperties.getTopics().getLikedPost();
        kafkaTemplate.send(topic, event);
        log.info("Sent LikeEvent to topic {}: {}", topic, event);
    }
}
