package faang.school.postservice.mapper.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JsonMapper<T> {
    private final ObjectMapper mapper = new ObjectMapper();

    public String convertObjectToJson(T object){
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Ошибка при преобразовании объекта в json: {}", e);
            throw new RuntimeException(e);
        }
    }
}