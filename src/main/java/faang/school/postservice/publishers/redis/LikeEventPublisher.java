package faang.school.postservice.publishers.redis;

import faang.school.postservice.events.Event;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikeEventPublisher extends AbstractMessagePublisher<Like, LikeEvent> {
    private final LikeEventMapper likeEventMapper;
    public LikeEventPublisher(ChannelTopic likePost,
                              RedisTemplate<String, Event> redisTemplate,
                              LikeEventMapper likeEventMapper) {
        super(likePost, redisTemplate);
        this.likeEventMapper = likeEventMapper;
    }

    @Override
    LikeEvent mapper(Like like) {
        return likeEventMapper.toEvent(like);
    }
}