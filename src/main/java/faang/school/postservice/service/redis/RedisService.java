package faang.school.postservice.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.PostForFeedDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.model.Post;
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

@Slf4j
@Service

public class RedisService {
    private static final String KEY_PREFIX_BY_POST = "post_";
    private static final String KEY_PREFIX_BY_AUTHOR_POST = "posts_by_author_";
    private static final String KEY_PREFIX_BY_POST_FEED = "post_feed_by_subscriber_";

    @Value("${spring.data.redis.post-feed.maximum-feed:500}")
    private Integer maxPostsPerSubscriber;

    @Value("${spring.data.redis.ttl.post}")
    private Long ttlAuthorPostInRedis;

    private final RedisTemplate<String, PostV2Dto> redisTemplatePosts;
    private final RedisTemplate<String, Object> redisTemplateAuthorPosts;
    private final RedisTemplate<String, Object> redisTemplatePostFeed;
    private final ObjectMapper objectMapperPostFeed;

    public RedisService(@Qualifier("redisTemplatePost") RedisTemplate<String, PostV2Dto> redisTemplatePosts,
                        @Qualifier("redisTemplateAuthorPost") RedisTemplate<String, Object> redisTemplateAuthorPosts,
                        @Qualifier("redisTemplateForKafkaPostConsumer") RedisTemplate<String, Object> redisTemplatePostFeed,
                        ObjectMapper objectMapperPostFeed) {
        this.redisTemplatePosts = redisTemplatePosts;
        this.redisTemplateAuthorPosts = redisTemplateAuthorPosts;
        this.redisTemplatePostFeed = redisTemplatePostFeed;
        this.objectMapperPostFeed = objectMapperPostFeed;
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

    public void savePostForFeed(PostForFeedDto postForFeedDto) {
        List<Long> subscriberIds = postForFeedDto.subscriberIds();
        for (Long id : subscriberIds) {
            String key = KEY_PREFIX_BY_POST_FEED + id;

          saveToRedis(key, postForFeedDto.postId());
        }
    }

    public List<Long> getSubscriberIdsForPost(Long subscriberIds) {
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
