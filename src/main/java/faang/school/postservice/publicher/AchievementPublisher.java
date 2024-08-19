package faang.school.postservice.publicher;

import faang.school.postservice.dto.notification.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final CommentEventMapper commentEventMapper;

    public void publish(Comment comment) {
        CommentEvent commentEvent =
                commentEventMapper.toEvent(comment);
        redisTemplate.convertAndSend(channelTopic.getTopic(), commentEvent);
    }
}
