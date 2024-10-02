package faang.school.postservice.producer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.producer.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaProducer<E> implements Producer<E> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topic;
    private final ObjectMapper objectMapper;

    @Override
    public void send(E event) {
        String jsonEvent;
        try {
            jsonEvent = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error(
                    String.format("An error occurred while serializing the event: %s.", event), e
            );
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(topic.name(), jsonEvent);
    }
}
