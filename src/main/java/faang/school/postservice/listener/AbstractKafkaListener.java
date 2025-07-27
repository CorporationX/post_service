package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.kafka.InvalidKafkaMessageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaListener<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> eventClass;

    protected T getEvent(String message) {
        try {
            return objectMapper.readValue(message, eventClass);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to deserialize Kafka message into class {}. Message: {}. Error: {}",
                    eventClass.getSimpleName(), message, ex.getMessage(), ex);
            throw new InvalidKafkaMessageException("Invalid message format", ex);
        }
    }
}
