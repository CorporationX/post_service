package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PostRedisRepository {
    private static final String KEY_PREFIX = "post:";
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonUtils jsonUtils;

    @Value("${spring.data.redis.post.post-ttl-hours}")
    private int ttl;

    public void savePost(Long postId, PostResponseDto postResponseDto) {
        String key = KEY_PREFIX + postId;
        redisTemplate.opsForValue().set(key, jsonUtils.toJson(postResponseDto), Duration.ofHours(ttl));
    }

    public PostResponseDto getPost(Long postId) {
        String key = KEY_PREFIX + postId;
        return jsonUtils.fromJson(redisTemplate.opsForValue().get(key), PostResponseDto.class);
    }

    public List<PostResponseDto> getAllPosts(Set<Long> postIds) {
        List<String> keys = postIds.stream()
                .map(id -> KEY_PREFIX + id)
                .toList();

        List<String> jsons = redisTemplate.opsForValue().multiGet(keys);
        return jsons.stream()
                .map(json -> jsonUtils.fromJson(json, PostResponseDto.class))
                .toList();
    }
}
