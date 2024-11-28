package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaSerializer {
    private final ObjectMapper objectMapper;

    public String serialize(Object eventDto) {
        try {
            String value = objectMapper.writeValueAsString(eventDto);
            log.debug("Serialized event {}", value);
            return value;
        } catch (JsonProcessingException e) {
            log.error("Event could not be serialized {}", eventDto);
            throw new RuntimeException(e);
        }
    }
}
