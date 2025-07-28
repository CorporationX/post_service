package faang.school.postservice.repository.post;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostCacheRepositoryImpl implements PostCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void set(PostOutputDto post) {
        redisTemplate.opsForHash().put(redisProperties.getCacheNames().posts(), String.valueOf(post.getId()), post);
        redisTemplate.expire(redisProperties.getCacheNames().posts(), redisProperties.getCacheDuration().posts());
    }

    @Override
    public PostOutputDto get(long postId) {
        return (PostOutputDto) redisTemplate.opsForHash().get(redisProperties.getCacheNames().posts(), String.valueOf(postId));
    }
}
