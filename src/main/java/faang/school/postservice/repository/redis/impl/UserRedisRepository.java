package faang.school.postservice.repository.redis.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.redis.UserRedisEntity;
import faang.school.postservice.repository.redis.AbstractRedisRepository;
import faang.school.postservice.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRedisRepository extends AbstractRedisRepository<UserRedisEntity> {

    private static final String PREFIX = "user:{}";

    private final HashOperations<String, String, String> opsForHash;

    public UserRedisRepository(
        StringRedisTemplate stringRedisTemplate,
        ObjectMapper objectMapper,
        Utils utils,
        @Value("${application.redis.user-time-to-live:100}") long timeToLive
    ) {
        super(stringRedisTemplate, objectMapper, utils, timeToLive);
        this.opsForHash = redisTemplate.opsForHash();
    }

    @Override
    public String add(UserRedisEntity userRedisEntity) {
        String key = getKey(PREFIX, userRedisEntity.getUserId());
        opsForHash.put(key, "user", getJsonText(userRedisEntity));
        return key;
    }
}
