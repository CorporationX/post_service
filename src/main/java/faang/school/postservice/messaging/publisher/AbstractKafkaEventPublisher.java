package faang.school.postservice.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import static faang.school.postservice.exception.ExceptionMessages.INSERTION_STAPLES;
import static faang.school.postservice.exception.ExceptionMessages.TOPIC_PUBLICATION_EXCEPTION;
import static faang.school.postservice.exception.ExceptionMessages.WRITING_TO_JSON_EXCEPTION;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaEventPublisher<T> implements EventPublisher<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    @Override
    public void publish(T event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicName, message);
            log.info("Published an event: {}", event);
        } catch (JsonProcessingException e) {
            log.error(WRITING_TO_JSON_EXCEPTION + INSERTION_STAPLES, e.getMessage());
        } catch (Exception e) {
            log.error(TOPIC_PUBLICATION_EXCEPTION  + INSERTION_STAPLES, e.getMessage());
        }
    }
}