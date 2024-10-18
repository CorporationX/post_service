package faang.school.postservice.publisher.comment;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher<CommentEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentEventChannel;

    @Override
    public void publish(CommentEventDto commentEventDto) {
        redisTemplate.convertAndSend(commentEventChannel.getTopic(), commentEventDto);
        log.debug("Sent message with id : {} in {}", commentEventDto.getCommentId(),
                commentEventChannel.getTopic());
    }
}
