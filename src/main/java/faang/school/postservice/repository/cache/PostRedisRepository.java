package faang.school.postservice.repository.cache;

import faang.school.postservice.config.redis.RedisKey;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository implements PostCacheRepository {
    private final RedisTemplate<String, PostEvent> redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public void cachePost(PostEvent value) {
        redisTemplate.opsForHash().put(RedisKey.POST.name(), value.getId(), value);
        redisTemplate.expire(RedisKey.POST.name(), cacheProperties.getPostTtl());
    }

    @Override
    public PostEvent getPost(Long id) {
        HashOperations<String, Long, PostEvent> hashOps =
                redisTemplate.opsForHash();

        return hashOps.get(RedisKey.POST.name(), id);
    }
}
