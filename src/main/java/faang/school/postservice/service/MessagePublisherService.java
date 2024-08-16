package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.redis.event.CommentEvent;
import faang.school.postservice.redis.publisher.CommentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisherService {

    private final CommentEventPublisher commentEventPublisher;

    public void publishCommentEvent(Comment savedComment, ObjectMapper objectMapper) {
        CommentEvent commentEvent = new CommentEvent();
        commentEvent.setPostId(savedComment.getPost().getId());
        commentEvent.setAuthorId(savedComment.getAuthorId());
        commentEvent.setCommentId(savedComment.getId());
        commentEvent.setSendAt(LocalDateTime.now());

        String message = null;
        try {
            message = objectMapper.writeValueAsString(commentEvent);
        } catch (JsonProcessingException e) {
            log.warn("There was an exception during conversion CommentEvent with ID = {} to String", savedComment.getId());
        }
        commentEventPublisher.publishMessage(message);
    }
}
