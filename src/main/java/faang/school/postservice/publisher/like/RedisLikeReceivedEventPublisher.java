package faang.school.postservice.publisher.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import faang.school.postservice.config.redis.RedisChannelsConfig;
import faang.school.postservice.dto.event.LikeReceivedEventDto;
import faang.school.postservice.publisher.RedisEventPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisLikeReceivedEventPublisher extends RedisEventPublisher<LikeReceivedEventDto> {
    @Autowired
    public RedisLikeReceivedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                        RedisChannelsConfig redisChannelsConfig) {
        super(redisTemplate, redisChannelsConfig);  // Explicit parent constructor call
    }

    public void publish(LikeReceivedEventDto event) {
        super.push(getRedisChannelsConfig().getLikeReceived(), event);
        log.info("Published event: {}.", event);
    }
}
