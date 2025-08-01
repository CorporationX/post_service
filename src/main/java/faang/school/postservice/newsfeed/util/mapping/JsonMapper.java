package faang.school.postservice.newsfeed.util.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.MappingException;
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
            throw new MappingException("Entity to json mapping exception", e);
        }
    }

    public <T> T mapToObject(String json, Class<T> tClass){
        try {
            return mapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            throw new MappingException("Json to entity mapping exception", e);
        }
    }
}
