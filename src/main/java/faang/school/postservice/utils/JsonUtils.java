package faang.school.postservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static faang.school.postservice.messages.ErrorMessages.ERROR_DESERIALIZING;
import static faang.school.postservice.messages.ErrorMessages.SERIALIZATION_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {
    private final ObjectMapper objectMapper;

    public <T> T deserialize(String jsonResponse, Class<T> classType) {
        try {
            return objectMapper.readValue(jsonResponse, classType);
        } catch (JsonProcessingException e) {
            log.error(ERROR_DESERIALIZING + classType.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(SERIALIZATION_ERROR, e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> toMap(Object obj) {
        Map<String, Object> intermediate = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
        return intermediate.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.valueOf(entry.getValue())
                ));
    }

    public <T> T convertMapToClass(Map<Object, Object> map, Class<T> classType) {
        Map<String, Object> stringMap = map.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));
        return objectMapper.convertValue(stringMap, classType);
    }
}
