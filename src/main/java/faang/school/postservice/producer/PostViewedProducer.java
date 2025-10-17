package faang.school.postservice.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostViewedProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPostViewedEvent(PostViewedEvent postViewedEvent) {
        try {
            String data = objectMapper.writeValueAsString(postViewedEvent);
            String topic = "post_views";
            log.info("Send new postEvent to Kafka: topic={}, data: {}", topic, data);
            kafkaTemplate.send(topic, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
