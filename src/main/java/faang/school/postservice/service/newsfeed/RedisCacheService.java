package faang.school.postservice.service.newsfeed;

import faang.school.postservice.config.properties.FeedProperties;
import faang.school.postservice.dto.newsfeed.KafkaCommentEvent;
import faang.school.postservice.dto.newsfeed.KafkaLikeEvent;
import faang.school.postservice.dto.newsfeed.KafkaPostViewEvent;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.exception.RedisCacheException;
import faang.school.postservice.model.redis.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> feedRedisTemplate;
    private final FeedProperties feedProperties;

    public void addToFeed(Long userId, KafkaTimePostIdEvent event) {
        String key = RedisKeyConstants.FEED_KEY_PREFIX.getValue() + userId;
        try {
            feedRedisTemplate.opsForZSet().add(key, event.id(), event.publishedAt());
            feedRedisTemplate.opsForZSet().removeRange(key, 0, -feedProperties.getMaxFeedSize() - 1);
            feedRedisTemplate.expire(key, feedProperties.getFeedTtl(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to add to feed for user {}", userId, e);
            throw new RedisCacheException("Failed to add to feed for user " + userId, e);
        }
    }

    public void addLikeToPost(KafkaLikeEvent like) {
        String key = RedisKeyConstants.POST_KEY_PREFIX.getValue() +
                like.postId() + RedisKeyConstants.LIKES_SUFFIX.getValue();
        try {
            feedRedisTemplate.opsForSet().add(key, like.userId());
            feedRedisTemplate.expire(key, feedProperties.getPostTtl(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to add like to post {}", like.postId(), e);
            throw new RedisCacheException("Failed to add like to post " + like.postId(), e);
        }
    }

    public void addViewToPost(KafkaPostViewEvent view) {
        String key = RedisKeyConstants.POST_KEY_PREFIX.getValue() +
                view.postId() + RedisKeyConstants.VIEWS_SUFFIX.getValue();
        try {
            feedRedisTemplate.opsForHyperLogLog().add(key, view.userId());
            feedRedisTemplate.expire(key, feedProperties.getPostTtl(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to add view to post {}", view.postId(), e);
            throw new RedisCacheException("Failed to add view to post " + view.postId(), e);
        }
    }

    public void addCommentToPost(KafkaCommentEvent comment) {
        String key = RedisKeyConstants.POST_KEY_PREFIX.getValue() +
                comment.postId() + RedisKeyConstants.COMMENTS_SUFFIX.getValue();
        try {
            feedRedisTemplate.opsForList().leftPush(key, comment);
            feedRedisTemplate.opsForList().trim(key, 0, feedProperties.getMaxComments() - 1);
            feedRedisTemplate.expire(key, feedProperties.getPostTtl(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to add comment to post {}", comment.postId(), e);
            throw new RedisCacheException("Failed to add comment to post " + comment.postId(), e);
        }
    }

    public boolean isAlreadyProcessed(UUID eventId) {
        String key = RedisKeyConstants.PROCESSED_PREFIX.getValue() + eventId;
        try {
            Boolean result = feedRedisTemplate.opsForValue().setIfAbsent(key, "1",
                    feedProperties.getEventIdTtl(), TimeUnit.SECONDS);
            return result != null && !result;
        } catch (Exception e) {
            log.error("Failed to check processed for event {}", eventId, e);
            throw new RedisCacheException("Failed to check processed for event " + eventId, e);
        }
    }

    public void markAsProcessed(UUID eventId) {
        String key = RedisKeyConstants.PROCESSED_PREFIX.getValue() + eventId;
        try {
            feedRedisTemplate.opsForValue().set(key, "1", feedProperties.getEventIdTtl(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to mark event {} as processed", eventId, e);
            throw new RedisCacheException("Failed to mark event as processed: " + eventId, e);
        }
    }
}
