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
import org.springframework.data.redis.core.RedisCallback;
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
        FeedCacheService {

    private static final String FEED_KEY_PREFIX = "feed:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedCacheProperties feedCacheProperties;
    private final UserServiceClient userServiceClient;

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
            postIds = redisTemplate.opsForZSet().reverseRangeByScore(
                    feedKey, 0, score - 1, 0, limit
            );
        }

        if (postIds == null) {
            log.warn("Post IDs are null for user feed {}", userId);
            return Collections.emptyList();
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
        String key = FEED_KEY_PREFIX + userId;
        redisTemplate.delete(key);

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            long score = System.currentTimeMillis();
            for (Long postId : postIds) {
                connection.zAdd(key.getBytes(), score--, postId.toString().getBytes());
            }
            return null;
        });
    }

    @Async("feedTaskExecutor")
    void addPostChunkToFeeds(Long postId, Long timestamp, List<Long> chunk) {
        try {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Long subscriberId : chunk) {
                    String feedKey = FEED_KEY_PREFIX + subscriberId;
                    byte[] keyBytes = feedKey.getBytes();
                    byte[] postIdBytes = postId.toString().getBytes();

                    connection.zAdd(keyBytes, timestamp, postIdBytes);
                    
                    long maxSize = feedCacheProperties.getMaxFeedSize();
                    connection.zRemRange(keyBytes, 0, -(maxSize + 1));
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to add post {} to {} feeds", postId, chunk.size(), e);
        }
    }

    private FeedPostDto getPostWithAuthor(Long postId) {
        String postKey = "post:" + postId;
        Object postObj = redisTemplate.opsForValue().get(postKey);

        if (postObj == null) {
            log.warn("Post {} not found in Redis", postId);
            return null;
        }

        PostRedisDto post = (PostRedisDto) postObj;
        Long authorId = post.getAuthorId();

        String userKey = "user:" + authorId;
        Object userObj = redisTemplate.opsForValue().get(userKey);

        if (userObj == null) {
            log.warn("Author {} not found in Redis", authorId);
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
