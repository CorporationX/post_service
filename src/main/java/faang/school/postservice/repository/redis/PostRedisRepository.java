package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.hash.PostHash;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public abstract class PostRedisRepository implements CrudRepository<PostHash, Long> {

    private final RedisTemplate<Long, PostHash> redisTemplate;

    public void saveInRedis(PostHash postHash) {
        redisTemplate.opsForZSet().add(
                postHash.getId(), postHash, postHash.getId());
    }
}
