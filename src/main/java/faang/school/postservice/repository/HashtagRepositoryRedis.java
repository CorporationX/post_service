package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HashtagRepositoryRedis {
    private static final String POPULAR_HASHTAGS_KEY = "popularHashtags";

    private final RedisTemplate<String, String> redisTemplate;

    public void incrementHashtag(String hashtag) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        zSetOps.incrementScore(POPULAR_HASHTAGS_KEY, hashtag, 1);
    }

    public Set<String> getTopHashtags(int count) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.reverseRange(POPULAR_HASHTAGS_KEY, 0, count - 1);
    }

    public Double getHashtagScore(String hashtag) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.score(POPULAR_HASHTAGS_KEY, hashtag);
    }
}
