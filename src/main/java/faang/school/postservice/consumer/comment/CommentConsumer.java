package faang.school.postservice.consumer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentConsumer {

    private final PostCacheRepository postCacheRepository;
    private final ObjectMapper objectMapper;
    private final CommentMapper commentMapper;

    @KafkaListener(topics = "comments", groupId = "comment-group")
    public void consume(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        CommentEvent event = objectMapper.readValue(message, CommentEvent.class);

        try {
            CommentDto commentDto = commentMapper.commentEventToDto(event);
            postCacheRepository.addComment(event.getPostId(), commentDto);
            acknowledgment.acknowledge();
            log.info("Successfully cache comment for post {} and acknowledged message", event.getPostId());
        } catch (Exception e) {
            log.error("Failed to process post {}: {}", event.getPostId(), e.getMessage());
        }
    }
}
