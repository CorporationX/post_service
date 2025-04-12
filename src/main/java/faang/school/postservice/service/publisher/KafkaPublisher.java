package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishEvent(String topic, Object object) {
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            log.error("Serialization error");
            throw new RuntimeException();
        }
        log.info("Sent object: {}, to topic: {}", object.getClass(), topic);
    }
}
