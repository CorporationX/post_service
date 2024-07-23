package faang.school.postservice.repository.redis.feed;

import faang.school.postservice.model.redis.RedisFeed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepositoryImpl implements RedisFeedRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public RedisFeed getById(Long id) {
        return (RedisFeed) redisTemplate.opsForHash().get(id.toString(), id);
    }

    @Override
    public void save(RedisFeed feedRedis) {
        redisTemplate.opsForHash().put(feedRedis.getId().toString(), feedRedis.getId(), feedRedis);
    }
}
