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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    private static final String KEY_PREFIX_BY_POST = "post_";
    private static final String KEY_PREFIX_BY_AUTHOR_POST = "posts_by_author_";
    private static final String KEY_PREFIX_BY_POST_FEED = "post_feed_by_subscriber_";
    private static final String KEY_PREFIX_BY_POST_COMMENTS = "post_comments_";

    @Value("${spring.data.redis.post-feed.maximum-feed:500}")
    private Integer maxPostsPerSubscriber;

    @Value("${post.comments.max-size:3}")
    private Integer maxCommentsPerPost;

    @Value("${spring.data.redis.ttl.post}")
    private Long ttlAuthorPostInRedis;

    private final RedisTemplate<String, PostV2Dto> redisTemplatePosts;
    private final RedisTemplate<String, Object> redisTemplateAuthorPosts;
    private final RedisTemplate<String, Object> redisTemplatePostFeed;
    private final RedisTemplate<String, Object> redisTemplatePostForComments;
    private final ObjectMapper objectMapperPostFeed;

    public RedisService(@Qualifier("redisTemplatePost") RedisTemplate<String, PostV2Dto> redisTemplatePosts,
                        @Qualifier("redisTemplateAuthorPost") RedisTemplate<String, Object> redisTemplateAuthorPosts,
                        @Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplatePostFeed,
                        @Qualifier("redisTemplatePostForComments") RedisTemplate<String, Object> redisTemplatePostForComments,
                        ObjectMapper objectMapperPostFeed) {
        this.redisTemplatePosts = redisTemplatePosts;
        this.redisTemplateAuthorPosts = redisTemplateAuthorPosts;
        this.redisTemplatePostFeed = redisTemplatePostFeed;
        this.objectMapperPostFeed = objectMapperPostFeed;
        this.redisTemplatePostForComments = redisTemplatePostForComments;
    }

    @Async("postEventTaskExecutor")
    public void savePostInRedis(PostV2Dto postV2Dto) {
        String key = KEY_PREFIX_BY_POST + postV2Dto.id();

        try {
            redisTemplatePosts.opsForValue().set(key, postV2Dto, ttlAuthorPostInRedis, TimeUnit.DAYS);
            log.info("Saved post into redis: {}", postV2Dto);
        } catch (Exception e) {
            log.error("Error while saving post into redis", e);
        }
    }

    @Async("postEventTaskExecutor")
    public void saveAuthorPosts(Post post) {
        try {
            redisTemplateAuthorPosts.opsForValue().set(KEY_PREFIX_BY_AUTHOR_POST + post.getId(), post.getAuthorId(), ttlAuthorPostInRedis, TimeUnit.DAYS);
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
}
