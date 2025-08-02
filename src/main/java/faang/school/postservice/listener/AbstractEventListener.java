package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> {

    protected final ObjectMapper objectMapper;

    protected T getEventDto(String message, Class<T> eventClass) {
        log.debug("get event from kafka.\n{}", message);
        try {
            return objectMapper.readValue(message, eventClass);
        } catch (JsonProcessingException e) {
            log.error(getJsonProcessingExceptionText(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public abstract String getJsonProcessingExceptionText();
}
