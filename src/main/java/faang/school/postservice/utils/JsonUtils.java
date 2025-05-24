package faang.school.postservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.JsonException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static faang.school.postservice.contants.ErrorMessage.FAILED_SERIALIZING_OBJECT;

@Component
@RequiredArgsConstructor
public class JsonUtils {
    private final ObjectMapper mapper;

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new JsonException(FAILED_SERIALIZING_OBJECT);
        }
    }

    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonException(FAILED_SERIALIZING_OBJECT);
        }
    }
}
