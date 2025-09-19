package faang.school.postservice.repository;

import faang.school.postservice.config.redis.entity.FeedRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FeedRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.feed-channel}")
    private String topicName;

    public void save(FeedRedis feedRedis) {
        redisTemplate.opsForHash().put(topicName + feedRedis.getUserId(), feedRedis.getUserId(), feedRedis);
    }

    public FeedRedis getById(Long id) {
        return (FeedRedis) redisTemplate.opsForHash().get(topicName + id, id);
    }
}
