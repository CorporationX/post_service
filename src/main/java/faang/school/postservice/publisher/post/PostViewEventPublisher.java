package faang.school.postservice.publisher.post;


import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewEventPublisher implements MessagePublisher<PostViewEvent> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.post_view_name}")
    private String topicName;

    @Override
    public void publish(PostViewEvent postViewEvent) {
        redisTemplate.convertAndSend(topicName, postViewEvent);
        log.info("Message published. Viewed post: {}", postViewEvent.toString());
    }
}
