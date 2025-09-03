package faang.school.postservice.kafka.listener.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.comment.PostCommentCache;
import faang.school.postservice.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostCommentPublishedListener {
    private final ObjectMapper objectMapper;
    private final PostCommentCache cache;

    @KafkaListener(topics = "${spring.kafka.topics.post-comment-published.name}")
    public void listen(String message) throws JsonProcessingException {
        CommentDto dto = objectMapper.readValue(message, CommentDto.class);
        cache.add(dto);
    }
}
