package faang.school.postservice.repository.ad;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PostRedisRepository {
    private static final String KEY_PREFIX = "post:";
    private final JsonUtils jsonUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.post.post-ttl-hours}")
    private int postTtl;

    public void savePost(Long id, PostResponseDto post) {
        redisTemplate.opsForValue().set(KEY_PREFIX + id, jsonUtils.toJson(post), Duration.ofHours(postTtl));
    }

    public PostResponseDto getPost(Long id) {
        return jsonUtils.fromJson(redisTemplate.opsForValue().get(KEY_PREFIX + id), PostResponseDto.class);
    }
}
