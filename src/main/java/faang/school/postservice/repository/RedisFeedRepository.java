package faang.school.postservice.repository;

import faang.school.postservice.model.redis.FeedRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.topic.feed.name}")
    private String topicName;

    public FeedRedis getById(Long id) {
        return (FeedRedis) redisTemplate.opsForHash().get(topicName + id, id);
    }

    public void save(FeedRedis feedRedis) {
        redisTemplate.opsForHash().put(topicName + feedRedis.getId(), feedRedis.getId(), feedRedis);
    }
}
