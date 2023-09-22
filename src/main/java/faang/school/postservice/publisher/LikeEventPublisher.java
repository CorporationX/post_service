package faang.school.postservice.publisher;


import faang.school.postservice.publisher.events.LikeEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class LikeEventPublisher {

    @Setter
    @Value("${spring.data.redis.channels.like_events_channel.name}")
    private String likeEventsChannel;

    private final RedisMessagePublisher redisMessagePublisher;

    @Autowired
    public LikeEventPublisher(RedisMessagePublisher redisMessagePublisher) {
        this.redisMessagePublisher = redisMessagePublisher;
    }

    public void publishLikeEvent(Long postId, Long postAuthorId, Long likeAuthorId, LocalDateTime createdAt) {
        LikeEvent likeEvent = LikeEvent.builder()
                .postId(postId)
                .postAuthorId(postAuthorId)
                .likeAuthorId(likeAuthorId)
                .createdAt(createdAt)
                .build();

        redisMessagePublisher.publish(likeEventsChannel, likeEvent);
    }
}
