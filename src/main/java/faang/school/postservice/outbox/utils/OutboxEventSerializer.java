package faang.school.postservice.outbox.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.exception.OutboxSerializationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventSerializer {
    private final ObjectMapper objectMapper;

    public String serialize(PostCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new OutboxSerializationException("Failed to serialize PostCreatedEvent for Outbox", e);
        }
    }
}