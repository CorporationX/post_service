package faang.school.postservice.service.kafka.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.producer.topics.user-ban}")
    private String topic;

    public void send(Object object) {
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(object));
            log.info("Published to kafka: {}", object);
        } catch (JsonProcessingException e) {
            log.error("Serialization error", e);
            throw new RuntimeException(e);
        }
    }
}
