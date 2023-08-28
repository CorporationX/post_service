package faang.school.postservice.messaging.CommentEventPublisher;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisCommentEventPublisher implements CommentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final JsonMapper jsonMapper;

    @Override
    public void publish(CommentEventDto commentEventDto) {
        jsonMapper.toObject(commentEventDto)
                .ifPresent(s -> redisTemplate.convertAndSend(topic.getTopic(), s));
    }
}
