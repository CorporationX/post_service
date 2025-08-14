package faang.school.postservice.repository.redis.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.redis.PostRedisEntity;
import faang.school.postservice.repository.redis.AbstractRedisRepository;
import faang.school.postservice.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostRedisRepository extends AbstractRedisRepository<PostRedisEntity> {

    private static final String PREFIX = "post:{}";

    private final HashOperations<String, String, String> opsForHash;

    public PostRedisRepository(
        StringRedisTemplate stringRedisTemplate,
        ObjectMapper objectMapper,
        Utils utils,
        @Value("${application.redis.post-time-to-live:100}") long timeToLive
    ) {
        super(stringRedisTemplate, objectMapper, utils, timeToLive);
        this.opsForHash = redisTemplate.opsForHash();
    }

    @Override
    public String add(PostRedisEntity postRedisEntity) {
        String key = getKey(PREFIX, postRedisEntity.getPostId());
        opsForHash.put(key, "post", getJsonText(postRedisEntity));
        return key;
    }
}
