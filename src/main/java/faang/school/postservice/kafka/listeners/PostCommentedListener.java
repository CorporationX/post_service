package faang.school.postservice.kafka.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.kafka.events.PostCommentedEvent;
import faang.school.postservice.redis.cache.service.RedisPostService;
import faang.school.postservice.redis.cache.service.RedisUserService;
import faang.school.postservice.service.PostService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PostCommentedListener extends AbstractEventListener<PostCommentedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;

    public PostCommentedListener(ObjectMapper objectMapper,
                                 PostService postService,
                                 RedisProperties redisProperties,
                                 RedisPostService redisPostService,
                                 RedisUserService redisUserService,
                                 RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, postService, redisProperties, redisPostService, redisUserService);
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Class<PostCommentedEvent> getEventClass() {
        return PostCommentedEvent.class;
    }

    @Override
    protected void processEvent(PostCommentedEvent event) {
        String redisKey = "post:" + event.getPostId() + ":comments";
        redisTemplate.opsForList().leftPush(redisKey, String.valueOf(event.getCommentId()));
        redisTemplate.opsForList().trim(redisKey, 0, redisProperties.getMaxCommentsToCache() - 1);
    }

    @KafkaListener(topics = "comment.made", groupId = "event-listeners")
    public void listen(String message, Acknowledgment ack) {
        super.handle(message);
        ack.acknowledge();
    }
}
