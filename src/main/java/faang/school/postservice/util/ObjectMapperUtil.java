package faang.school.postservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    public String asString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new DataValidationException("Failed to serialize object");
        }
    }

    public <T> T as(String object, Class<T> type) {
        try {
            return objectMapper.readValue(object, type);
        } catch (JsonProcessingException e) {
            throw new DataValidationException("Failed to parse json");
        }
    }
}