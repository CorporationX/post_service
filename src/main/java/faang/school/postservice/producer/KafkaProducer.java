package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(String topic, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            log.info("Message with {} json event publish", json);
            kafkaTemplate.send(topic, json)
                    .thenRunAsync(() -> log.info("Sent message: {}", message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
