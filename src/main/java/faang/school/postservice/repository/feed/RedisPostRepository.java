package faang.school.postservice.repository.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {
    private static final String POST_KEY_PREFIX = "post:";
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    public void savePost() {

    }
}
