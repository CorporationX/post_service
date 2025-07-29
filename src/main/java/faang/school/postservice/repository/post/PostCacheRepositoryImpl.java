package faang.school.postservice.repository.post;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostCacheRepositoryImpl implements PostCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private final PostMapper postMapper;

    @Override
    public void set(PostOutputDto post) {
        redisTemplate.opsForHash().put(redisProperties.getCacheNames().posts(),
                String.valueOf(post.getId()), postMapper.toCacheDto(post));
        redisTemplate.expire(redisProperties.getCacheNames().posts(), redisProperties.getCacheDuration().posts());
    }

    @Override
    public PostCacheDto get(long postId) {
        HashOperations<String, String, PostCacheDto> hashOps = redisTemplate.opsForHash();

        return hashOps.get(redisProperties.getCacheNames().posts(), String.valueOf(postId));
    }
}
