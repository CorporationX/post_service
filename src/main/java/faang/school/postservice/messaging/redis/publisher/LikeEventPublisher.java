package faang.school.postservice.messaging.redis.publisher;


import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.messaging.redis.events.LikeEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public void publishLikeEvent(Like like, Post post) {
        LikeEvent likeEvent = LikeEvent.builder()
                .postId(post.getId())
                .postAuthorId(post.getAuthorId())
                .likeAuthorId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();

        redisMessagePublisher.publish(likeEventsChannel, likeEvent);
    }
}
