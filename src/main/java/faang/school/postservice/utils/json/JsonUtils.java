package faang.school.postservice.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.JsonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static faang.school.postservice.contants.ErrorMessage.FAILED_SERIALIZING_OBJECT;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {
    private final ObjectMapper objectMapper;

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error(FAILED_SERIALIZING_OBJECT, e);
            throw new JsonException(FAILED_SERIALIZING_OBJECT);
        }
    }

    public String toJson( Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(FAILED_SERIALIZING_OBJECT, e);
            throw new JsonException(FAILED_SERIALIZING_OBJECT);
        }
    }

}
