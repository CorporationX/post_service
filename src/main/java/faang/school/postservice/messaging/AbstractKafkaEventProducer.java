package faang.school.postservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class AbstractKafkaEventProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final ObjectMapper mapper;

    public void publish(T event) {
        try {
            kafkaTemplate.send(topic, mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Can't converting object to string - {}", event);
            throw new RuntimeException("Can't converting object to string");
        }
    }
}
