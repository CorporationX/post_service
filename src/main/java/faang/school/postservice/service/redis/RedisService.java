package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.PostV2Dto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service

public class RedisService {
    private static final String KEY_PREFIX_BY_POST = "post_";

    @Value("${spring.data.redis.ttl.post}")
    private Long ttlAuthorPostInRedis;

    private final RedisTemplate<String, PostV2Dto> redisTemplatePosts;

    public RedisService(@Qualifier("redisTemplatePost") RedisTemplate<String, PostV2Dto> redisTemplatePosts) {
        this.redisTemplatePosts = redisTemplatePosts;
    }

    public void savePostInRedis(PostV2Dto postV2Dto) {
        String key = KEY_PREFIX_BY_POST + postV2Dto.id();

        try {
            redisTemplatePosts.opsForValue().set(key, postV2Dto, ttlAuthorPostInRedis, TimeUnit.DAYS);
            log.info("Saved post into redis: {}", postV2Dto);
        } catch (Exception e) {
            log.error("Error while saving post into redis", e);
        }
    }
}
