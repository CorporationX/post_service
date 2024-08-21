package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicationService {
    private final CommentEventPublisher commentEventPublisher;
    private final ObjectMapper objectMapper;

    public void publishCommentEvent(CommentEvent event) throws JsonProcessingException {
        commentEventPublisher.publish(toJson(event));
    }

    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
