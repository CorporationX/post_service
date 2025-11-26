package faang.school.postservice.messaging;

import faang.school.postservice.dto.event.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.name.comment_event}")
    private String channel;

    public void publish(CommentEventDto commentEventDto) {
        log.info("Publishing comment event notification, authorId: {}, receiverId: {}, commentId: {}, postId: {}",
                commentEventDto.authorId(), commentEventDto.receivedId(),
                commentEventDto.commentId(), commentEventDto.postId());
        redisTemplate.convertAndSend(channel, commentEventDto);
        log.info("Comment event notification sent");
    }
}