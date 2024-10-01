package faang.school.postservice.repository.redis;


import faang.school.postservice.dto.post.CachedPostDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public class RedisPostRepository extends AbstractRedisRepository<CachedPostDto>{
    private static final String CACHE_PREFIX = "post:";
    public RedisPostRepository(RedisTemplate<String, Object> redisTemplate,
                               @Value("${spring.data.redis.TTL}") long timeToLive) {
        super(CACHE_PREFIX, redisTemplate, timeToLive);
    }

    @Override
    public void saveAll(List<CachedPostDto> postDtos) {
        for (CachedPostDto postDto : postDtos) {
            save(postDto.getId(), postDto);
        }
    }
}
