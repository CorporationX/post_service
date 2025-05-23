package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostCreatedEvent;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    public void sendPostCreatedEvent(PostCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            String topic = kafkaProperties.getTopics().getPostCreated();
            kafkaTemplate.send(topic, event.getPostId().toString(), json);
            log.info("PostCreatedEvent в Kafka для поста {}", event.getPostId());
        } catch (Exception e) {
            log.error("❌ Не удалось отправить событие PostCreatedEvent", e);
        }
    }
}
