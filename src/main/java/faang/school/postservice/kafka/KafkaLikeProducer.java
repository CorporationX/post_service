package faang.school.postservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.AbstractKafkaProducer;
import faang.school.postservice.dto.like.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaLikeProducer extends AbstractKafkaProducer {
    @Value("${spring.kafka.producer.topics.like}")
    private String likeTopic;

    public KafkaLikeProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    public void sendLikeEvent(LikeEvent likeEvent) {
        try {
            log.info("Attempting to send LikeEvent to topic '{}': {}", likeTopic, likeEvent);
            super.send(likeTopic, likeEvent);
            log.info("Successfully sent LikeEvent to topic '{}': {}", likeTopic, likeEvent);
        } catch (Exception e) {
            log.error("Failed to send LikeEvent to topic '{}': {}. Error: {}", likeTopic, likeEvent, e.getMessage());
            throw e;
        }
    }
}
