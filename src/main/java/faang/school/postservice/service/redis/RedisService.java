package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.PostV2Dto;
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
    private static final String KEY_PREFIX_BY_POST = "post_";
    private static final String KEY_PREFIX_BY_AUTHOR_POST = "posts_by_author_";

    @Value("${spring.data.redis.ttl.post}")
    private Long ttlAuthorPostInRedis;

    private final RedisTemplate<String, PostV2Dto> redisTemplatePosts;
    private final RedisTemplate<String, Object> redisTemplateAuthorPosts;

    public RedisService(@Qualifier("redisTemplatePost") RedisTemplate<String, PostV2Dto> redisTemplatePosts,
                        @Qualifier("redisTemplateAuthorPost") RedisTemplate<String, Object> redisTemplateAuthorPosts) {
        this.redisTemplatePosts = redisTemplatePosts;
        this.redisTemplateAuthorPosts = redisTemplateAuthorPosts;
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

    public void saveAuthorPosts(Post post) {
        try {
            redisTemplateAuthorPosts.opsForValue().set(KEY_PREFIX_BY_AUTHOR_POST + post.getId(), post.getAuthorId(), ttlAuthorPostInRedis, TimeUnit.DAYS);
            log.info("Author {} posts {} saved to redis", post.getAuthorId(), post.getId());
        } catch (Exception e) {
            log.error("Error while saving author posts", e);
        }

    }
}
