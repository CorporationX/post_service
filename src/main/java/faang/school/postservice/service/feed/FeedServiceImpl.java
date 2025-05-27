package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedServiceImpl implements
        FeedRetrievalService,
        FeedManagementService,
        FeedCacheService,
        FeedAsyncUpdater {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedCacheProperties feedCacheProperties;
    private final UserServiceClient userServiceClient;

    private static final String FEED_KEY_PREFIX = "feed:";

    @Override
    public void addPostToFeeds(Long postId, Long timestamp, List<Long> subscriberIds) {
        int chunkSize = 1000;
        for (int i = 0; i < subscriberIds.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, subscriberIds.size());
            List<Long> chunk = subscriberIds.subList(i, end);
            addPostChunkToFeeds(postId, timestamp, chunk);
        }
    }

    @Override
    public List<FeedPostDto> getFeed(Long userId, Long cursorPostId, int limit) {
        String feedKey = FEED_KEY_PREFIX + userId;

        Set<Object> postIds;

        if (cursorPostId == null) {
            postIds = redisTemplate.opsForZSet().reverseRange(feedKey, 0, limit - 1);
        } else {
            Double score = redisTemplate.opsForZSet().score(feedKey, cursorPostId);
            if (score == null) {
                return Collections.emptyList();
            }
            postIds = redisTemplate.opsForZSet().reverseRangeByScore(feedKey, score - 1, 0, 0, limit);
        }

        return postIds.stream()
                .map(id -> getPostWithAuthor((Long) id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void cachePostAndAuthor(Post post) {
        cachePost(post);
        cacheAuthor(post.getAuthorId());
    }

    public void rebuildFeed(Long userId, List<Long> postIds) {
        String key = "feed:" + userId;
        redisTemplate.delete(key);
        long score = System.currentTimeMillis();

        for (Long postId : postIds) {
            redisTemplate.opsForZSet().add(key, postId, score--);
        }
    }

    @Override
    @Async("feedTaskExecutor")
    public void addPostChunkToFeeds(Long postId, Long timestamp, List<Long> chunk) {
        for (Long subscriberId : chunk) {
            String feedKey = FEED_KEY_PREFIX + subscriberId;
            try {
                redisTemplate.opsForZSet().add(feedKey, postId, timestamp);

                long maxFeedSize = feedCacheProperties.getMaxFeedSize();
                redisTemplate.opsForZSet().removeRange(feedKey, 0, -(maxFeedSize + 1));
            } catch (Exception e) {
                log.error("Не удалось добавить пост {} в фид {}", postId, subscriberId, e);
            }
        }
    }

    private FeedPostDto getPostWithAuthor(Long postId) {
        String postKey = "post:" + postId;
        Object postObj = redisTemplate.opsForValue().get(postKey);

        if (postObj == null) {
            log.warn("Пост {} не найден в Redis", postId);
            return null;
        }

        PostRedisDto post = (PostRedisDto) postObj;
        Long authorId = post.getAuthorId();

        String userKey = "user:" + authorId;
        Object userObj = redisTemplate.opsForValue().get(userKey);

        if (userObj == null) {
            log.warn("Автор {} не найден в Redis", authorId);
            return null;
        }

        UserRedisDto user = (UserRedisDto) userObj;

        return FeedPostDto.builder()
                .id(postId)
                .text(post.getText())
                .authorId(authorId)
                .authorName(user.getName())
                .likeCount(post.getLikeCount())
                .build();
    }

    private void cachePost(Post post) {
        PostRedisDto dto = PostRedisDto.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .text(post.getContent())
                .projectId(post.getProjectId())
                .likeCount((long) post.getLikes().size())
                .createdAt(post.getCreatedAt())
                .build();
        redisTemplate.opsForValue().set("post:" + post.getId(), dto, Duration.ofDays(1));
    }

    private void cacheAuthor(Long authorId) {
        if (!redisTemplate.hasKey("user:" + authorId)) {
            UserDto user = userServiceClient.getUser(authorId);
            UserRedisDto dto = UserRedisDto.builder()
                    .id(user.id())
                    .name(user.username())
                    .build();
            redisTemplate.opsForValue().set("user:" + authorId, dto, Duration.ofDays(1));
        }
    }
}
