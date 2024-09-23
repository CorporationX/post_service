package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer {
    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.posts}")
    private String postsTopic;

    public void publish(Long authorId, List<Long> subscriberIds) {
        try {
            kafkaTemplate.send(postsTopic, authorId, subscriberIds);
            log.info("Successfully published to Kafka. {}", subscriberIds.size());
        } catch (Exception e) {
            log.error("Failed to publish to Kafka");
            throw new RuntimeException("Error while publishing to Kafka", e);
        }
    }
}
