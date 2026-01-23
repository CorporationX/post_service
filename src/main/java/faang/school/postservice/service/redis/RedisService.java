package faang.school.postservice.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.CommentEventDto;
import faang.school.postservice.dto.kafka.PostForFeedDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    private static final String KEY_PREFIX_BY_POST = "post_";
    private static final String KEY_PREFIX_BY_AUTHOR_POST = "posts_by_author_";
    private static final String KEY_PREFIX_BY_POST_FEED = "post_feed_by_subscriber_";
    private static final String KEY_PREFIX_BY_POST_COMMENTS = "post_comments_";
    private static final String KEY_PREFIX_BY_AUTHOR_COMMENT = "comment_by_author_";
    private static final String KEY_POST_LIKES_COUNT = "post_likes_count_";
    private static final String KEY_POST_LIKED_BY = "post_liked_by_";

    @Value("${spring.data.redis.post-feed.maximum-feed:500}")
    private Integer maxPostsPerSubscriber;

    @Value("${post.comments.max-size:3}")
    private Integer maxCommentsPerPost;

    @Value("${spring.data.redis.ttl.post}")
    private Long ttlAuthorInRedis;

    private final RedisTemplate<String, PostV2Dto> redisTemplatePosts;
    private final RedisTemplate<String, Object> redisTemplateAuthorPosts;
    private final RedisTemplate<String, Object> redisTemplatePostFeed;
    private final RedisTemplate<String, Object> redisTemplatePostForComments;
    private final ObjectMapper objectMapperPostFeed;
    private final RedisTemplate<String, Object> redisTemplateAuthorComment;
    private final DefaultRedisScript<Long> incrementLikesScript;
    private final DefaultRedisScript<Long> decrementLikesScript;

    public RedisService(@Qualifier("redisTemplatePost") RedisTemplate<String, PostV2Dto> redisTemplatePosts,
                        @Qualifier("redisTemplateAuthorPost") RedisTemplate<String, Object> redisTemplateAuthorPosts,
                        @Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplatePostFeed,
                        @Qualifier("redisTemplatePostForComments") RedisTemplate<String, Object> redisTemplatePostForComments,
                        @Qualifier("redisTemplateAuthorComment") RedisTemplate<String, Object> redisTemplateAuthorComment,
                        ObjectMapper objectMapperPostFeed,
                        @Qualifier("incrementLikesScript") DefaultRedisScript<Long> incrementLikesScript,
                        @Qualifier("decrementLikesScript") DefaultRedisScript<Long> decrementLikesScript) {
        this.redisTemplatePosts = redisTemplatePosts;
        this.redisTemplateAuthorPosts = redisTemplateAuthorPosts;
        this.redisTemplatePostFeed = redisTemplatePostFeed;
        this.objectMapperPostFeed = objectMapperPostFeed;
        this.redisTemplatePostForComments = redisTemplatePostForComments;
        this.redisTemplateAuthorComment = redisTemplateAuthorComment;
        this.incrementLikesScript = incrementLikesScript;
        this.decrementLikesScript = decrementLikesScript;
    }

    @Async("postEventTaskExecutor")
    public void savePostInRedis(PostV2Dto postV2Dto) {
        String key = KEY_PREFIX_BY_POST + postV2Dto.id();

        try {
            redisTemplatePosts.opsForValue().set(key, postV2Dto, ttlAuthorInRedis, TimeUnit.DAYS);
            log.info("Saved post into redis: {}", postV2Dto);
        } catch (Exception e) {
            log.error("Error while saving post into redis", e);
        }
    }

    @Async("postEventTaskExecutor")
    public void saveAuthorPosts(Post post) {
        try {
            redisTemplateAuthorPosts.opsForValue().set(KEY_PREFIX_BY_AUTHOR_POST + post.getId(), post.getAuthorId(), ttlAuthorInRedis, TimeUnit.DAYS);
            log.info("Author {} posts {} saved to redis", post.getAuthorId(), post.getId());
        } catch (Exception e) {
            log.error("Error while saving author posts", e);
        }
    }

    @Async("postEventTaskExecutor")
    public void savePostForFeed(PostForFeedDto postForFeedDto) {
        List<Long> subscriberIds = postForFeedDto.subscriberIds();
        for (Long id : subscriberIds) {
            String key = KEY_PREFIX_BY_POST_FEED + id;
            saveToRedis(key, postForFeedDto.postId());
        }
    }

    @Async("postEventTaskExecutor")
    public void savePostForComments(CommentEventDto event) {
        String key = KEY_PREFIX_BY_POST_COMMENTS + event.postId();
        try {
            redisTemplatePostForComments.opsForZSet().add(key, event.commentId(), -System.currentTimeMillis());
            Long currentSize = redisTemplatePostForComments.opsForZSet().size(key);
            if (currentSize != null && currentSize >= maxCommentsPerPost) {
                redisTemplatePostForComments.opsForZSet().removeRange(key, maxCommentsPerPost, -1);
            }
        } catch (Exception e) {
            log.error("Error while saving post for subscriber {}", event.postId(), e);
        }
    }

    @Async("postEventTaskExecutor")
    public void saveAuthorComment(CommentEventDto event) {
        try {
            redisTemplateAuthorComment.opsForValue().set(KEY_PREFIX_BY_AUTHOR_COMMENT + event.commentId(), event.commentAuthorId(), ttlAuthorInRedis, TimeUnit.DAYS);
            log.info("Author {} comment {} saved to redis", event.commentAuthorId(), event.postId());
        } catch (Exception e) {
            log.error("Error while saving author comment", e);
        }
    }

    public List getPostsByUserId(Long subscriberIds) {
        Object value = redisTemplatePostFeed.opsForZSet().range(KEY_PREFIX_BY_POST_FEED + subscriberIds, 0, -1);
        if (value != null) {
            return objectMapperPostFeed.convertValue(value, List.class);
        } else {
            return new ArrayList<>();
        }
    }

    private void saveToRedis(String key, Long postId) {

        redisTemplatePostFeed.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);

                Long currentSize = operations.opsForZSet().size(key);
                operations.multi();

                operations.opsForZSet().add(key, postId, -System.currentTimeMillis());

                if (currentSize != null && currentSize >= maxPostsPerSubscriber) {
                    operations.opsForZSet().removeRange(key, maxPostsPerSubscriber, -1);
                }
                try {
                    operations.exec();
                } catch (Exception e) {
                    operations.discard();
                    throw e;
                }
                return null;
            }
        });
    }

    @Async("postEventTaskExecutor")
    public CompletableFuture<Long> incrementPostLikes(Long postId, Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            String countKey = KEY_POST_LIKES_COUNT + postId;
            String userLikesKey = KEY_POST_LIKED_BY + postId;

            String luaScript = """
            local countKey = KEYS[1]
            local userLikesKey = KEYS[2]
            local userId = ARGV[1]
            
            local alreadyLiked = redis.call("SISMEMBER", userLikesKey, userId)
            if alreadyLiked == 1 then
                local current = redis.call("GET", countKey)
                return tonumber(current) or 0
            end
            
            redis.call('SADD', userLikesKey, userId)
            local newCount = redis.call('INCR', countKey)
            return newCount
            """;

            try {
                DefaultRedisScript<Long> script = new DefaultRedisScript<>();
                script.setScriptText(luaScript);
                script.setResultType(Long.class);

                List<String> keys = Arrays.asList(countKey, userLikesKey);
                Long result = redisTemplatePostFeed.execute(script, keys, userId.toString());

                if (result == null) {
                    log.error("Lua script returned null for post {}", postId);
                    return 0L;
                }

                log.debug("Incremented likes for post {} to {}", postId, result);
                updatePostLikesCount(postId, result);

                return result;
            } catch (Exception e) {
                log.error("Error incrementing likes for post {}: {}", postId, e.getMessage(), e);
                return 0L;
            }
        });
    }

    @Async("postEventTaskExecutor")
    public CompletableFuture<Long> decrementPostLikes(Long postId, Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            String countKey = KEY_POST_LIKES_COUNT + postId;
            String userLikesKey = KEY_POST_LIKED_BY + postId;

            String luaScript = """
            local countKey = KEYS[1]
            local userLikesKey = KEYS[2]
            local userId = ARGV[1]
            
            -- Проверяем, лайкал ли пользователь
            local alreadyLiked = redis.call('SISMEMBER', userLikesKey, userId)
            if alreadyLiked == 0 then
                local current = redis.call('GET', countKey)
                return tonumber(current) or 0
            end
            
            -- Удаляем пользователя из множества лайкнувших
            redis.call('SREM', userLikesKey, userId)
            
            -- Уменьшаем счетчик лайков
            local newCount = redis.call('DECR', countKey)
            
            -- Не позволяем счетчику уйти в минус
            if newCount < 0 then
                redis.call('SET', countKey, 0)
                return 0
            end
            
            return newCount
            """;

            try {
                DefaultRedisScript<Long> script = new DefaultRedisScript<>();
                script.setScriptText(luaScript);
                script.setResultType(Long.class);

                List<String> keys = Arrays.asList(countKey, userLikesKey);
                Long result = redisTemplatePostFeed.execute(script, keys, userId.toString());

                if (result == null) {
                    log.error("Lua script returned null for post {}", postId);
                    return 0L;
                }

                log.debug("Decremented likes for post {} to {}", postId, result);
                updatePostLikesCount(postId, result);

                return result;
            } catch (Exception e) {
                log.error("Error decrementing likes for post {}: {}", postId, e.getMessage(), e);
                return 0L;
            }
        });
    }

    @Async("postEventTaskExecutor")
    public void updatePostLikesCount(Long postId, Long newLikesCount) {
        String postKey = KEY_PREFIX_BY_POST + postId;

        try {
            redisTemplatePosts.execute(new SessionCallback<Object>() {
                @Override
                @SuppressWarnings("unchecked")
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.watch(postKey);

                    PostV2Dto post = (PostV2Dto) operations.opsForValue().get(postKey);

                    if (post == null) {
                        log.debug("Post {} not found in Redis, skipping full update", postId);
                        operations.unwatch();
                        return null;
                    }

                    operations.multi();

                    try {
                        PostV2Dto updatedPost = PostV2Dto.builder()
                                .id(post.id())
                                .content(post.content())
                                .authorId(post.authorId())
                                .projectId(post.projectId())
                                .likesIds(post.likesIds())
                                .commentsIds(post.commentsIds())
                                .albumsIds(post.albumsIds())
                                .adId(post.adId())
                                .resourcesIds(post.resourcesIds())
                                .published(post.published())
                                .publishedAt(post.publishedAt())
                                .scheduledAt(post.scheduledAt())
                                .createdAt(post.createdAt())
                                .updatedAt(post.updatedAt())
                                .likesCount(newLikesCount)
                                .build();

                        operations.opsForValue().set(postKey, updatedPost);

                        // Сохраняем TTL если он был установлен
                        Long ttl = operations.getExpire(postKey);
                        if (ttl != null && ttl > 0) {
                            operations.expire(postKey, ttl, TimeUnit.SECONDS);
                        } else if (ttl != null && ttl == -1) {

                        }

                        List<Object> results = operations.exec();
                        if (results != null && !results.isEmpty()) {
                            log.debug("Updated post {} in Redis with new likes count: {}",
                                    postId, newLikesCount);
                        } else {
                            log.debug("Transaction was discarded for post {}", postId);
                        }

                    } catch (Exception e) {
                        operations.discard();
                        log.error("Transaction failed for post {} update: {}",
                                postId, e.getMessage(), e);
                        throw e;
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("Error updating post likes count in Redis for post {}: {}",
                    postId, e.getMessage(), e);
        }
    }

}
