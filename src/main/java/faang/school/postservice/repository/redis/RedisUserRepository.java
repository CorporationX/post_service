package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CachedUser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisUserRepository {
    private static final String USER_PREFIX = "user_";
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.ttl-hours}")
    private long ttlHours;
    private HashOperations<String, String, CachedUser> hashOpsPosts;

    @PostConstruct
    public void init() {
        hashOpsPosts = redisTemplate.opsForHash();
    }

    public void saveUser(CachedUser user) {
        hashOpsPosts.put(USER_PREFIX + user.getId(), user.getId().toString(), user);
        redisTemplate.expire(USER_PREFIX + user.getId(), ttlHours, TimeUnit.HOURS);
        log.info("Saved user in Redis");
    }
}
