package faang.school.postservice.publisher.like;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import faang.school.postservice.config.redis.RedisChannelsConfig;
import faang.school.postservice.dto.event.LikeReceivedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisChannelsConfig.class)
public class RedisLikeReceivedEventPublisherImpl implements RedisLikeReceivedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisChannelsConfig redisChannelsConfig;

    @Override
    public void publish(LikeReceivedEventDto event) {

        long returnCode = redisTemplate.convertAndSend(redisChannelsConfig.getLikeReceived(), event);
        log.info("Published event: {}, with code {}.", event, returnCode);
    }
}
