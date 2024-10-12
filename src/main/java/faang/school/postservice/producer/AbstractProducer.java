package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public class AbstractProducer<T> {

    private final NewTopic topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(T event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic.name(), eventJson);
            log.info("To %s topic was sent message: %s".formatted(topic.name(), eventJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse event: %s to JSON".formatted(event));
        }
    }
}
