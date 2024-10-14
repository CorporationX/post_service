package faang.school.postservice.repository.feed;

import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {
    private static final String POST_KEY_PREFIX = "post:";
    private final RedisTemplate<String, Object> cacheRedisTemplate;

    @Value("${spring.data.redis.cache.ttl.post}")
    private long postTtl;

    public void savePost(PostDto postDto) {
        String key = POST_KEY_PREFIX + postDto.getId();
        cacheRedisTemplate.opsForValue().set(key, postDto, Duration.ofSeconds(postTtl));
    }

    public PostDto getPost(Long postId) {
        String key = POST_KEY_PREFIX + postId;
        return (PostDto) cacheRedisTemplate.opsForValue().get(key);
    }
}
