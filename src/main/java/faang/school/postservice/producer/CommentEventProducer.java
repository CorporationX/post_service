package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventProducer {
    private final ObjectMapper objectMapper;
    private final KafkaProperty kafkaProperty;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(CommentEvent event) {
        try {
            String data = objectMapper.writeValueAsString(event);
            String topic = kafkaProperty.topic().commentNew();
            log.info("Send new event id={} to Kafka: topic={}, data: {}", event.id(), topic, data);
            kafkaTemplate.send(topic, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
