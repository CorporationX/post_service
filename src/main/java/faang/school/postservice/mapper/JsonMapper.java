package faang.school.postservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonMapper {
    private final ObjectMapper mapper;

    public <T> String mapToJson(T entity) {
        try {
            return mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            log.error("Entity to json mapping exception", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T mapToObject(String json, Class<T> clazz){
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
           log.error("Json to entity mapping exception", e);
           throw new RuntimeException(e);
        }
    }

    public  <T> T convertToObject(Object fromValue, Class<T> toValueType) {
        return mapper.convertValue(fromValue, toValueType);
    }

}
