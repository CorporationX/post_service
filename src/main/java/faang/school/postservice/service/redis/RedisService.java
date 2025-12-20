package faang.school.postservice.service.redis;

import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    private static final String KEY_PREFIX_BY_AUTHOR_POST = "post_";

    @Value("${spring.data.redis.ttl.author-post}")
    private Long ttlAuthorPostInRedis;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(@Qualifier("redisTemplateAuthorPost") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveAuthorPosts(Post post) {
        try {
            redisTemplate.opsForValue().set(KEY_PREFIX_BY_AUTHOR_POST + post.getId(), post.getAuthorId(), ttlAuthorPostInRedis, TimeUnit.DAYS);
            log.info("Author {} posts {} saved to redis", post.getAuthorId(), post.getId());
        } catch (Exception e) {
            log.error("Error while saving author posts", e);
        }

    }
}
