package faang.school.postservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class JsonMapper {
    private ObjectMapper objectMapper;

    public <T> Optional<String> toObject(T event) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(event);
        } catch (IOException e) {
            log.error("Exception with json mapping: " + e.getMessage());
        }
        return Optional.ofNullable(result);
    }
}
