package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbstractEventPublisher<T> implements MessagePublisher<T> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic newTopic;

    public void publish(T event) {
        try {
            kafkaTemplate.send(newTopic.name(), objectMapper.writeValueAsString(event));
            log.info("Comment event published: {}", event);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
