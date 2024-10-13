package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventListener {
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleMessage(String jsonEvent) {
        LikeEvent event = readEvent(jsonEvent);
        log.info("Received message from channel: {}", jsonEvent);
    }

    private LikeEvent readEvent(String jsonEvent) {
        try {
            log.info("reading message {}", jsonEvent);
            return objectMapper.readValue(jsonEvent, LikeEvent.class);
        } catch (JsonProcessingException exception) {
            log.error("message was not downloaded {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}