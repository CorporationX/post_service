package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class UserCacheRepositoryImpl implements UserCacheRepository {

    private final RedisTemplate<String, UserDto> redisTemplate;

    private final String prefix;

    private final Duration ttl;

    public UserCacheRepositoryImpl(
            RedisTemplate<String, UserDto> redisTemplate,
            @Value("${cache.author.key.prefix}") String prefix,
            @Value("${cache.author.entry-ttl-hours}") Duration ttl
    ) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
        this.ttl = ttl;
    }

    @Override
    public Optional<UserDto> get(long authorId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(authorId)));
    }

    @Override
    public void save(long authorId, UserDto dto) {
        redisTemplate.opsForValue().set(key(authorId), dto, ttl);
    }

    private String key(long authorId) {
        return String.format("%s%s", prefix, authorId);
    }
}