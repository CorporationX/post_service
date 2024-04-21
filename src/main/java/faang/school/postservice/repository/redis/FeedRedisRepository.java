package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.hash.FeedHash;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public abstract class FeedRedisRepository implements CrudRepository<FeedHash, Long> {

    private final RedisTemplate<Long, FeedHash> redisTemplate;

    public void saveInRedis(FeedHash feedHash) {
        redisTemplate.opsForZSet().add(
                feedHash.getFeedId(), feedHash, feedHash.getFeedId());
    }
}
