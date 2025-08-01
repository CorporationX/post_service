package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.UserCacheDto;
import faang.school.postservice.newsfeed.util.mapping.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UsersRedisCacheRepository implements UsersCacheRepository {
    public static final String USER_KEY_PREFIX = "user:";
    public static final TimeUnit TTL_TIME_UNIT = TimeUnit.HOURS;

    private final RedisTemplate<String, String> redisTemplate;
    private final JsonMapper jsonMapper;

    @Value("${news-feed.cache.user_ttl}")
    private Long ttl;

    @Override
    public void putUser(UserCacheDto user) {
        String json = jsonMapper.mapToJson(user);
        String key = USER_KEY_PREFIX + user.getUserId();
        redisTemplate.opsForValue().set(key, json, ttl, TTL_TIME_UNIT);
    }

    @Override
    public Optional<UserCacheDto> getUserById(Long id) {
        String key = USER_KEY_PREFIX + id;
        String jsonUser = redisTemplate.opsForValue().getAndExpire(key, ttl, TTL_TIME_UNIT);
        return Optional.ofNullable(jsonUser)
                .map(json -> jsonMapper.mapToObject(json, UserCacheDto.class));
    }
}
