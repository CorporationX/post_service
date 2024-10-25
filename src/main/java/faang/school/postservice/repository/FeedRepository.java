package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBatch;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedRepository {
    @Value("${spring.data.redis.cache.user-posts.feed.key}")
    private String feedKey;

    @Value("${spring.data.redis.cache.user-posts.max-size}")
    private Integer userPostsCacheMaxSize;
    private final RedisTemplate<String, Long> feedRedisTemplate;
    private final RedissonClient redissonClient;
    private final PostRepository postRepository;

    @Transactional
    public void addPost(long userId, long postId) {
        String key = feedKey.formatted(userId);
        RLock lock = redissonClient.getLock("%s:lock".formatted(key));

        try {
            lock.lock();
            double currentTime = System.currentTimeMillis();
            feedRedisTemplate.opsForZSet().add(key, postId, currentTime);

            Long postCount = feedRedisTemplate.opsForZSet().zCard(key);
            if (postCount != null && postCount > userPostsCacheMaxSize) {
                feedRedisTemplate.opsForZSet().removeRange(key, 0, postCount - userPostsCacheMaxSize - 1);
            }
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void addPostToUsers(long postId, List<Long> userIds) {
        double currentTime = System.currentTimeMillis();
        RBatch batch = redissonClient.createBatch();

        for (Long userId : userIds) {
            String key = feedKey.formatted(userId);
            RLock lock = redissonClient.getLock("%s:lock".formatted(key));

            try {
                lock.lock();
                batch.getScoredSortedSet(key, LongCodec.INSTANCE).addAsync(currentTime, postId);
                RScoredSortedSet<Long> scoredSet = redissonClient.getScoredSortedSet(key);
                if (scoredSet.size() > userPostsCacheMaxSize) {
                    scoredSet.removeRangeByRankAsync(0, scoredSet.size() - userPostsCacheMaxSize - 1);
                }
            } finally {
                lock.unlock();
            }
        }

        batch.execute();
    }

    @Transactional
    public Collection<Long> getNextBatch(long userId, Long lastPostId, int limit) {
        String key = feedKey.formatted(userId);

        if (lastPostId == null) {
            return feedRedisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        } else {
            Double lastPostScore = feedRedisTemplate.opsForZSet().score(key, lastPostId);
            if (lastPostScore != null) {
                return feedRedisTemplate.opsForZSet()
                        .reverseRangeByScore(key, lastPostScore, Double.NEGATIVE_INFINITY, 1, limit);
            } else {
                Pageable pageable = PageRequest.of(0, limit);
                return postRepository.findNextBatch(userId, pageable).stream()
                        .map(Post::getId).toList();
            }
        }
    }
}
