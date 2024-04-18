package faang.school.postservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonMapper {
    private final ObjectMapper mapper;

    public <T> String toJson(T event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Object mapping of a list is not successful", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T toEvent(String json, Class<T> eventClass) {
        try {
            return mapper.readValue(json, eventClass);
        } catch (JsonProcessingException e) {
            log.error("Deserialization failed", e);
            throw new RuntimeException(e);
        }
    }
}
