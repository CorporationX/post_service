package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractProducer<T> {

   private final KafkaTemplate<String, String> kafkaTemplate;
   private final String topic;
   private final ObjectMapper objectMapper;

    public void sendMessage(T event) {
        try {
            log.info("Event publication: {}", event);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
            log.error("Error event sending. Event: {}", event, e);
            throw new RuntimeException("Error converting object to json.", e);
        }
    }
}
