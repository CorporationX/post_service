package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventProducer {
    private final ObjectMapper objectMapper;
    private final KafkaProperty kafkaProperty;
    private final KafkaTemplate kafkaTemplate;

    public void sendPostEvent(PostEvent event) {
        try{
            String data = objectMapper.writeValueAsString(event);
            String topic = "postEvent";
            log.info("Send new postEvent to Kafka: topic={}, data: {}", topic, data);
            kafkaTemplate.send(topic, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
