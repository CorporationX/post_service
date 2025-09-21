package faang.school.postservice.repository.redis.feed;

import faang.school.postservice.model.Post;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FeedCacheRepository {

    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisTemplate<String, String> myStringRedisTemplate;
    private static final String FEED_PREFIX = "feed:";

    @Value("${cache.feed.max-records}")
    private int maxRecords;

    private static final DefaultRedisScript<Void> ADD_AND_TRIM = new DefaultRedisScript<>(
            """
            redis.call('ZADD', KEYS[1], ARGV[2], ARGV[1])
            redis.call('ZREMRANGEBYRANK', KEYS[1], 0, ARGV[3])
            return
            """, Void.class);

    public void addPostToFeed(Long userId, Post post) {
        String key = FEED_PREFIX + userId;
        int trim = -maxRecords -1;
        double score = getScorePublishedDescIdDesc(post);
        longRedisTemplate.execute(ADD_AND_TRIM, List.of(key),
                post.getId().toString(), String.valueOf(score), String.valueOf(trim));
    }

    public void addPostsBatched(Long userId, List<Post> posts){
        Set<ZSetOperations.TypedTuple<String>> batch = new HashSet<>();

        for (Post post: posts){
            batch.add(new DefaultTypedTuple<>(post.getId().toString(), getScorePublishedDescIdDesc(post)));
        }

        myStringRedisTemplate.opsForZSet().add(FEED_PREFIX + userId, batch);
        log.info("Set batch of {} post IDs as feed for user {}", batch.size(), userId);
    }

    private double getScorePublishedDescIdDesc(@NonNull Post post) {
        long timePart = post.getPublishedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long idPart = post.getId();

        return timePart + (idPart % 1000) / 1000.0;
    }

    public void addPostToFeedOld(Long userId, Long postId, LocalDateTime publishedAt) {
        String key = FEED_PREFIX + userId;
        double score = publishedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        longRedisTemplate.opsForZSet().add(key, postId, score);
    }

    public List<Long> getFeedAfter(Long userId, Long afterPostId, int limit) {
        String key = FEED_PREFIX + userId;
        var zset = longRedisTemplate.opsForZSet();

        // First page when no anchor is provided
        if (afterPostId == null) {
            Set<Long> ids = zset.reverseRange(key, 0, Math.max(0, limit - 1));
            return ids == null ? List.of() : new ArrayList<>(ids);
        }

        Long rank = zset.reverseRank(key, afterPostId);
        if (rank == null) {
            // Anchor not in the feed – fallback to first page
            Set<Long> ids = zset.reverseRange(key, 0, Math.max(0, limit - 1));
            return ids == null ? List.of() : new ArrayList<>(ids);
        }

        Set<Long> ids = zset.reverseRange(key, rank + 1, rank + limit);
        return ids == null ? List.of() : new ArrayList<>(ids);
    }

//    public List<Long> getFeed(Long userId, int offset, int limit) {
//        String key = FEED_PREFIX + userId;
//        // ZREVRANGE: get posts sorted by publishedAt descending
//        Set<Long> postIds = longRedisTemplate.opsForZSet()
//                .reverseRange(key, offset, offset + limit - 1);
//        return postIds != null ? new ArrayList<>(postIds) : List.of();
//    }

    public void removePostFromFeed(Long userId, Long postId) {
        String key = FEED_PREFIX + userId;
        longRedisTemplate.opsForZSet().remove(key, postId);
    }

    public void evictFeed(Long userId) {
        longRedisTemplate.delete(FEED_PREFIX + userId);
    }
}

