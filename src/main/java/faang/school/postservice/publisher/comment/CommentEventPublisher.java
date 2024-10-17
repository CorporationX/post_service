package faang.school.postservice.publisher.comment;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Builder
@Slf4j
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher<CommentEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentEventChannel;

    @Override
    public void publish(CommentEventDto message) {
        redisTemplate.convertAndSend(commentEventChannel.getTopic(), message);
        log.debug("Sent message with id : {} in {}", message.getCommentId(),
                commentEventChannel.getTopic());
    }
}
