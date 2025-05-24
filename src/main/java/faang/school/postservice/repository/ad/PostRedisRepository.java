package faang.school.postservice.repository.ad;

import faang.school.postservice.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PostRedisRepository {
    private static final String KEY_PREFIX = "post:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.post.post-ttl-hours}")
    private int postTtl;

    public void savePost(Long id, PostResponseDto post) {
        redisTemplate.opsForValue().set(KEY_PREFIX + id, post, Duration.ofHours(postTtl));
    }

    public PostResponseDto getPost(Long id) {
        return (PostResponseDto) redisTemplate.opsForValue().get(KEY_PREFIX + id);
    }
}
