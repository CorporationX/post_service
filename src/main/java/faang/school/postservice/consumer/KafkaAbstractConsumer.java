package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationException;
import org.springframework.kafka.support.Acknowledgment;

@RequiredArgsConstructor
public abstract class KafkaAbstractConsumer<T> {

    private final ObjectMapper objectMapper;

    public void listen(String message, Class<T> clazz, Acknowledgment ack) {
        try {
            T event = objectMapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Can't serialize this event");
        }
    }
}
