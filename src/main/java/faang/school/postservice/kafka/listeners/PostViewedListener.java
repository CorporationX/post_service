package faang.school.postservice.kafka.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.kafka.events.PostViewedEvent;
import faang.school.postservice.redis.cache.model.RedisPost;
import faang.school.postservice.redis.cache.service.RedisPostService;
import faang.school.postservice.redis.cache.service.RedisUserService;
import faang.school.postservice.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostViewedListener extends AbstractEventListener<PostViewedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;

    public PostViewedListener(ObjectMapper objectMapper,
                              PostService postService,
                              RedisProperties redisProperties,
                              RedisPostService redisPostService,
                              RedisUserService redisUserService, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, postService, redisProperties, redisPostService, redisUserService);
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Class<PostViewedEvent> getEventClass() {
        return PostViewedEvent.class;
    }

    @Override
    protected void processEvent(PostViewedEvent event) {
        String key = "post:" + event.getPostId() + ":viewCount";
        Long newCount = redisTemplate.opsForValue().increment(key);

        log.info("Incremented view count for post {}: {}", event.getPostId(), newCount);

        RedisPost redisPost = redisPostService.findByPostId(event.getPostId());
        if (redisPost != null) {
            redisPost.setViewCount(newCount.intValue());
            redisPostService.savePost(redisPost);
        }
    }

    @KafkaListener(topics = "post.viewed", groupId = "event-listeners")
    public void listen(String message) {
        super.handle(message);
    }
}
