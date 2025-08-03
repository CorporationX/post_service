package faang.school.postservice.kafka.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.kafka.events.PostLikedEvent;
import faang.school.postservice.redis.cache.model.RedisPost;
import faang.school.postservice.redis.cache.service.RedisPostService;
import faang.school.postservice.redis.cache.service.RedisUserService;
import faang.school.postservice.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostLikedListener extends AbstractEventListener<PostLikedEvent> {
    public PostLikedListener(ObjectMapper objectMapper,
                             PostService postService,
                             RedisProperties redisProperties,
                             RedisPostService redisPostService,
                             RedisUserService redisUserService) {
        super(objectMapper, postService, redisProperties, redisPostService, redisUserService);
    }

    @Override
    protected Class<PostLikedEvent> getEventClass() {
        return PostLikedEvent.class;
    }

    @Override
    protected void processEvent(PostLikedEvent event) {
        RedisPost post = redisPostService.findByPostId(event.getPostID());
        post.getLikeIds().add(event.getPostID());
        redisPostService.savePost(post);
    }

    @KafkaListener(topics = "post.liked", groupId = "event-listeners")
    public void listen(String message) {
        super.handle(message);
    }
}
