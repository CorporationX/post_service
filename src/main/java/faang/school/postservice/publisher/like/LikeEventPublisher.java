package faang.school.postservice.publisher.like;

import faang.school.postservice.config.properties.RedisProperties;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.MessagePublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements MessagePublisher<Like> {
    private static final String TOPIC_NAME = "like_event_name";

    private final RedisProperties properties;
    private final RedisTemplate<String, Object> redisTemplate;
    private String topic;
    private final LikeEventMapper likeEventMapper;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_NAME);
    }

    @Override
    public void publish(Like like) {
        LikeEvent likeEvent = likeEventMapper.likeToEvent(like);
        redisTemplate.convertAndSend(topic, likeEvent);
        log.info("Like event for post: {} has been successfully published.", likeEvent.postId());
    }
}

