package faang.school.postservice.consumer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AbstractConsumer<T> {

    protected final ObjectMapper objectMapper;
    protected final Class<T> clazz;

    public T convertMessageToObject(String message) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
