package faang.school.postservice.publisher.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import faang.school.postservice.config.redis.RedisChannelsConfig;
import faang.school.postservice.dto.event.PostCreateEventDto;
import faang.school.postservice.publisher.RedisEventPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisPostCreateEventPublisher extends RedisEventPublisher<PostCreateEventDto> {
    @Autowired
    public RedisPostCreateEventPublisher (RedisTemplate<String, Object> redisTemplate,
                                        RedisChannelsConfig redisChannelsConfig) {
        super(redisTemplate, redisChannelsConfig);  // Explicit parent constructor call
    }

    public void publish(PostCreateEventDto event) {
        super.push(getRedisChannelsConfig().getPostCreated(), event);
        log.info("Published event: {}.", event);
    }
}
