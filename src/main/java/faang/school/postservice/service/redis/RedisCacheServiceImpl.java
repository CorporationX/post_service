package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostRedis;
import faang.school.postservice.dto.user.UserNFDto;
import faang.school.postservice.event.PostCommentEvent;
import faang.school.postservice.exception.RedisTransactionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private final Environment environment;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final RedisScript<Long> POST_INCREMENT_VIEWS_SCRIPT = RedisScript.of(
            """
                    local key = KEYS[1]
                    local ttl = tonumber(ARGV[1])
                                
                    local post = redis.call('GET', key)
                    if not post then
                        return 0
                    end
                                
                    local postObj = cjson.decode(post)
                    postObj.views = postObj.views + 1
                    redis.call('SET', key, cjson.encode(postObj))
                    redis.call('EXPIRE', key, ttl)
                                
                    return 1
                    """,
            Long.class
    );

    private static final RedisScript<Long> POST_INCREMENT_LIKES_SCRIPT = RedisScript.of(
            """
                    local key = KEYS[1]
                    local ttl = tonumber(ARGV[1])
                                
                    local post = redis.call('GET', key)
                    if not post then
                        return 0
                    end
                                
                    local postObj = cjson.decode(post)
                    postObj.likes = postObj.likes + 1
                    redis.call('SET', key, cjson.encode(postObj))
                    redis.call('EXPIRE', key, ttl)
                                
                    return 1
                    """,
            Long.class
    );

    private static final RedisScript<Long> POST_ADD_COMMENT_SCRIPT = RedisScript.of(
            """
                    local key = KEYS[1]
                    local commentJson = ARGV[1]
                                        
                    local post = redis.call('GET', key)
                    if not post then
                        return 0
                    end
                                        
                    local postObj = cjson.decode(post)
                    local comment = cjson.decode(commentJson)
                    if not postObj.comments then
                        postObj.comments = {}
                    end
                    table.insert(postObj.comments, comment)
                                        
                    redis.call('SET', key, cjson.encode(postObj))
                    
                    return 1
                    """,
            Long.class
    );

    @Override
    public void savePost(PostRedis post) {
        redisTemplate.opsForValue().set(
                environment.getRequiredProperty("spring.data.redis.prefix-posts") + post.getId(),
                post,
                Duration.ofDays(Long.parseLong(environment.getRequiredProperty("spring.data.redis.ttl-posts")))
        );
    }

    @Override
    public void saveUser(UserNFDto user) {
        redisTemplate.opsForValue().set(
                environment.getRequiredProperty("spring.data.redis.prefix-users") + user.getId(),
                user,
                Duration.ofDays(Long.parseLong(environment.getRequiredProperty("spring.data.redis.ttl-users")))
        );
    }


    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = RedisTransactionFailedException.class
    )
    @Override
    public void incrementPostViews(Long postId) {
        String key = environment.getRequiredProperty("spring.data.redis.prefix-posts") + postId;
        int ttl = Integer.parseInt(environment.getRequiredProperty("spring.data.redis.ttl-posts")) * 86400;
        try {
            Long updatedViews = redisTemplate.execute(
                    POST_INCREMENT_VIEWS_SCRIPT,
                    Collections.singletonList(key),
                    ttl
            );

            if (updatedViews == null || updatedViews == 0) {
                log.warn("Post {} not found in Redis", postId);
            } else {
                log.info("Updated views for post {}: {}", postId, updatedViews);
            }
        } catch (RedisTransactionFailedException e) {
            log.error("Redis transaction failed", e);
            throw e;
        }
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = RedisTransactionFailedException.class
    )
    @Override
    public void incrementLike(Long postId) {
        String key = environment.getRequiredProperty("spring.data.redis.prefix-posts") + postId;
        int ttl = Integer.parseInt(environment.getRequiredProperty("spring.data.redis.ttl-posts")) * 86400;
        try {
            Long updatedLikes = redisTemplate.execute(
                    POST_INCREMENT_LIKES_SCRIPT,
                    Collections.singletonList(key),
                    ttl
            );

            if (updatedLikes == null || updatedLikes == 0) {
                log.warn("Post {} not found in Redis", postId);
            } else {
                log.info("Updated likes for post {}: {}", postId, updatedLikes);
            }
        } catch (RedisTransactionFailedException e) {
            log.error("Redis transaction failed", e);
            throw e;
        }
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = RedisTransactionFailedException.class
    )
    @Override
    public void addCommentForPost(PostCommentEvent event) {
        String key = environment.getRequiredProperty("spring.data.redis.prefix-posts") + event.getPostId();
        try {
            String commentJson = objectMapper.writeValueAsString(event);
            Long updatedComments = redisTemplate.execute(
                    POST_ADD_COMMENT_SCRIPT,
                    Collections.singletonList(key),
                    commentJson
            );
            if (updatedComments == null || updatedComments == 0) {
                log.warn("Post {} not found in Redis", event.getPostId());
            } else {
                log.info("Updated comments for post {}: {}", event.getPostId(), updatedComments);
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing comment", e);
            throw new RuntimeException("Error serializing comment", e);
        } catch (RedisTransactionFailedException e) {
            log.error("Redis transaction failed", e);
            throw e;
        }
    }
}
