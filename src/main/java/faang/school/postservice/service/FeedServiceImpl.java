package faang.school.postservice.service;

import faang.school.postservice.cache.PostCacheRepositoryImpl;
import faang.school.postservice.cache.UserCacheRepositoryImpl;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.FeedRedisProperties;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.feed.FeedPostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.feed.FeedPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FeedDbRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private static final DefaultRedisScript<Long> ADD_AND_TRIM_SCRIPT;

    static {
        ADD_AND_TRIM_SCRIPT = new DefaultRedisScript<>();
        ADD_AND_TRIM_SCRIPT.setResultType(Long.class);
        ADD_AND_TRIM_SCRIPT.setScriptText(
                "local added = redis.call('ZADD', KEYS[1], 'NX', ARGV[1], ARGV[2]) "
                        + "local size = redis.call('ZCARD', KEYS[1]) "
                        + "local maxSize = tonumber(ARGV[3]) "
                        + "if size > maxSize then "
                        + "  local extra = size - maxSize "
                        + "  redis.call('ZREMRANGEBYRANK', KEYS[1], 0, extra - 1) "
                        + "end "
                        + "return added"
        );
    }

    private final FeedDbRepository feedDbRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;

    private final PostCacheRepositoryImpl postCacheRepository;
    private final UserCacheRepositoryImpl userCacheRepository;

    private final FeedPostMapper feedPostMapper;

    private final StringRedisTemplate redis;
    private final FeedRedisProperties props;

    @Override
    public void addPostToFeed(long followerId, long postId, Instant occurredAt) {
        String key = buildKey(followerId);
        long score = occurredAt.toEpochMilli() * 1_000 + (postId % 1_000);

        Long added = redis.execute(
                ADD_AND_TRIM_SCRIPT,
                List.of(key),
                String.valueOf(score),
                String.valueOf(postId),
                String.valueOf(props.getMaxSize())
        );

        if (added == 1) {
            log.debug("Post added to feed: followerId={}, postId={}, score={}, key={}",
                    followerId, postId, score, key);
        } else {
            log.debug("Duplicate PostCreatedEvent ignored: followerId={}, postId={}, key={}",
                    followerId, postId, key);
        }
    }

    @Override
    public List<Long> getLatestPosts(long followerId, int limit) {
        String key = buildKey(followerId);

        var range = redis.opsForZSet().reverseRange(key, 0, limit - 1);
        if (range == null || range.isEmpty()) {
            return List.of();
        }
        return range.stream().map(Long::parseLong).toList();
    }

    @Override
    public List<FeedPostResponseDto> getFeed(long userId, Long afterPostId, int limit) {
        log.info("Feed request: userId={}, after={}, limit={}", userId, afterPostId, limit);
        List<Long> redisIds = loadFeedIds(userId, afterPostId, limit);
        List<Post> dbPosts = loadDbFallback(userId, afterPostId, redisIds, limit);
        List<Long> finalIds = mergeIds(redisIds, dbPosts, limit);

        Map<Long, PostCacheDto> postsById = loadPostsWithCache(finalIds);
        Map<Long, UserDto> usersById = loadUsersWithCache(postsById.values());

        return mapToDto(finalIds, postsById, usersById);
    }

    private List<Long> loadFeedIds(long userId, Long afterPostId, int limit) {
        return (afterPostId == null)
                ? getLatestPosts(userId, limit)
                : getPostsAfter(userId, afterPostId, limit);
    }

    private List<Post> loadDbFallback(long userId, Long afterPostId, List<Long> redisIds, int limit) {
        int missing = limit - redisIds.size();
        if (missing <= 0) {
            return List.of();
        }

        FeedCursor cursor = resolveDbCursor(afterPostId, redisIds);

        if (afterPostId != null && cursor.isEmpty()) {
            return List.of();
        }

        List<Long> followeeIds = getFolloweeIds(userId);
        if (followeeIds.isEmpty()) {
            return List.of();
        }

        PageRequest pr = PageRequest.of(0, missing);

        if (cursor.isEmpty()) {
            return feedDbRepository.findFeedPostsFirstPage(followeeIds, pr);
        }

        return feedDbRepository.findFeedPostsAfterCursor(
                followeeIds,
                cursor.createdAt(),
                cursor.id(),
                pr
        );
    }

    private List<Long> mergeIds(List<Long> redisIds, List<Post> dbPosts, int limit) {
        LinkedHashSet<Long> ordered = new LinkedHashSet<>(limit);
        for (Long id : redisIds) {
            if (ordered.size() >= limit) {
                break;
            }
            ordered.add(id);
        }
        for (Post p : dbPosts) {
            if (ordered.size() >= limit) {
                break;
            }
            ordered.add(p.getId());
        }
        return new ArrayList<>(ordered);
    }

    private Map<Long, PostCacheDto> loadPostsWithCache(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        Map<Long, PostCacheDto> cached = postCacheRepository.findAllByIds(ids);

        log.debug("cache keys types = {}", cached.keySet().stream()
                .map(k -> k.getClass().getName())
                .distinct().toList());
        log.debug("cache keys = {}", cached.keySet());

        List<Long> missingIds = ids.stream()
                .filter(id -> !cached.containsKey(id))
                .toList();

        if (missingIds.isEmpty()) {
            return cached;
        }

        List<Post> dbPosts = postRepository.findAllByIdIn(missingIds);

        log.debug("missingIds={}", missingIds);
        log.debug("dbPosts ids={}", dbPosts.stream().map(Post::getId).toList());
        log.debug("dbPosts published flags = {}", dbPosts.stream().map(Post::isPublished).toList());
        log.debug("dbPosts deleted flags = {}", dbPosts.stream().map(Post::isDeleted).toList());

        Map<Long, PostCacheDto> dbAsCache = dbPosts.stream()
                .map(this::toCacheDto)
                .collect(Collectors.toMap(PostCacheDto::id, p -> p, (a, b) -> a));

        postCacheRepository.saveAll(dbAsCache);

        Map<Long, PostCacheDto> result = new HashMap<>(cached);
        result.putAll(dbAsCache);
        return result;
    }

    private Map<Long, UserDto> loadUsersWithCache(Collection<PostCacheDto> posts) {
        List<Long> authorIds = posts.stream()
                .map(PostCacheDto::authorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (authorIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, UserDto> cached = userCacheRepository.findAllByIds(authorIds);

        List<Long> missingIds = authorIds.stream()
                .filter(id -> !cached.containsKey(id))
                .toList();

        if (missingIds.isEmpty()) {
            return cached;
        }

        List<UserDto> fetched = userServiceClient.getUsersByIds(missingIds);

        Map<Long, UserDto> fetchedById = fetched.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(UserDto::id, u -> u, (a, b) -> a));

        userCacheRepository.saveAll(fetchedById);

        Map<Long, UserDto> result = new HashMap<>(cached);
        result.putAll(fetchedById);
        return result;
    }

    private List<FeedPostResponseDto> mapToDto(
            List<Long> ids,
            Map<Long, PostCacheDto> postsById,
            Map<Long, UserDto> usersById
    ) {
        List<FeedPostResponseDto> result = new ArrayList<>(ids.size());

        for (Long id : ids) {
            PostCacheDto post = postsById.get(id);
            if (post == null) {
                continue;
            }
            UserDto author = (post.authorId() == null) ? null : usersById.get(post.authorId());
            result.add(feedPostMapper.toDtoSafe(post, author));
        }

        return result;
    }

    // ---------------- helpers ----------------

    private List<Long> getFolloweeIds(long userId) {
        return userServiceClient.getFollowees(
                        userId,
                        null,
                        null,
                        0,
                        Integer.MAX_VALUE
                ).stream()
                .filter(Objects::nonNull)
                .map(UserDto::id)
                .filter(Objects::nonNull)
                .toList();
    }

    private FeedCursor resolveDbCursor(Long afterPostId, List<Long> redisIds) {
        Long cursorPostId = (afterPostId != null)
                ? afterPostId
                : (redisIds.isEmpty() ? null : redisIds.get(redisIds.size() - 1));

        if (cursorPostId == null) {
            return FeedCursor.empty();
        }

        // cache-first: try Redis posts cache
        PostCacheDto cached = postCacheRepository.findAllByIds(List.of(cursorPostId)).get(cursorPostId);
        if (cached != null && cached.createdAt() != null) {
            return new FeedCursor(cached.createdAt(), cached.id());
        }

        // fallback: DB
        Post cursorPost = postRepository.findById(cursorPostId).orElse(null);
        if (cursorPost == null || cursorPost.getCreatedAt() == null) {
            return FeedCursor.empty();
        }

        // optional: write-through into cache (so next time cursor is cheap)
        postCacheRepository.saveAll(Map.of(cursorPostId, toCacheDto(cursorPost)));

        return new FeedCursor(cursorPost.getCreatedAt(), cursorPost.getId());
    }

    private record FeedCursor(LocalDateTime createdAt, Long id) {
        static FeedCursor empty() {
            return new FeedCursor(null, null);
        }

        boolean isEmpty() {
            return createdAt == null || id == null;
        }
    }

    private List<Long> getPostsAfter(long followerId, long afterPostId, int limit) {
        String key = buildKey(followerId);

        Double afterScore = redis.opsForZSet().score(key, String.valueOf(afterPostId));
        if (afterScore == null) {
            return List.of(); // DB fallback will handle
        }

        var range = redis.opsForZSet().reverseRangeByScore(
                key,
                Double.NEGATIVE_INFINITY,
                Math.nextDown(afterScore),
                0,
                limit
        );

        if (range == null || range.isEmpty()) {
            return List.of();
        }
        return range.stream().map(Long::parseLong).toList();
    }

    private String buildKey(long followerId) {
        return props.getKeyPrefix() + followerId;
    }

    private PostCacheDto toCacheDto(Post p) {
        return new PostCacheDto(
                p.getId(),
                p.getAuthorId(),
                p.getProjectId(),
                p.getContent(),
                p.isPublished(),
                p.isDeleted(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getPublishedAt(),
                p.getScheduledAt()
        );
    }
}
