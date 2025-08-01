package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.PostCacheDto;
import faang.school.postservice.newsfeed.util.mapping.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PostRedisCacheRepository implements PostCacheRepository {
    public static final String POST_KEY_PREFIX = "post:";
    public static final TimeUnit TTL_TIME_UNIT = TimeUnit.HOURS;

    private final RedisTemplate<String, String> redisTemplate;
    private final JsonMapper jsonMapper;

    @Value("${news-feed.cache.post_ttl}")
    private Long ttl;

    @Override
    public void putPost(PostCacheDto post) {
        String json = jsonMapper.mapToJson(post);
        String key = POST_KEY_PREFIX + post.getId();
        redisTemplate.opsForValue().set(key, json, ttl, TTL_TIME_UNIT);
    }

    @Override
    public Optional<PostCacheDto> getPostById(Long id) {
        String key = POST_KEY_PREFIX + id;
        String jsonPost = redisTemplate.opsForValue().getAndExpire(key, ttl, TTL_TIME_UNIT);
        return Optional.ofNullable(jsonPost)
                .map(json -> jsonMapper.mapToObject(json, PostCacheDto.class));
    }
}
