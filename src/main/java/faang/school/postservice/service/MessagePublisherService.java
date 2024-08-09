package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentEvent;
import faang.school.postservice.redis.CommentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisherService {

    private final CommentEventPublisher commentEventPublisher;

    public void publishCommentEvent(Comment savedComment) throws JsonProcessingException {
        CommentEvent commentEvent = new CommentEvent();
        commentEvent.setPostId(savedComment.getPost().getId());
        commentEvent.setAuthorId(savedComment.getAuthorId());
        commentEvent.setCommentId(savedComment.getId());
        commentEvent.setSendAt(LocalDateTime.now());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String message = objectMapper.writeValueAsString(commentEvent);
        commentEventPublisher.publishMessage(message);
    }
}
