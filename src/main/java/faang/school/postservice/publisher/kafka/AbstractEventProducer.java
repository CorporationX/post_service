package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public void sendEvent(T event) {
        try {
            log.info("Отправка ивента {} в Kafka с топиком {}", event, topicName);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicName, json);
        } catch (JsonProcessingException e) {
            log.info("Ошибка отправки ивента {} в Kafka с топиком {}", event, topicName);
            throw new RuntimeException("Ошибка конвертации в json", e);
        }
    }
}
