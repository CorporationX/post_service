package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.model.Feed;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate;
    private final FeedCacheRepository cacheRepository;

    @Value("${feed.cache.count}")
    private int maxCountOfPostsInFeed;

    @Override
    @Transactional
    public void distributePostsToUsersFeeds(PostPublishedEvent event) {
        List<Long> subsIds = event.getSubscribersIds();

        subsIds.forEach(id -> updateFeed(id, event.getPostId()));
    }

    @Retryable(maxAttempts = 5, retryFor = {OptimisticEntityLockException.class})
    private void updateFeed(long userId, long postId) {
        Feed feed = cacheRepository.findById(userId).orElse(new Feed(userId, new LinkedHashSet<>()));

        redisTemplate.watch(userId);
        try {
            addNewPostToFeed(feed, postId);
            cacheRepository.save(feed);
        } catch (Exception e) {
            redisTemplate.discard();
            throw e;
        }
        
        if (redisTemplate.exec().isEmpty()) {
            throw new OptimisticEntityLockException(feed, "(((");
        }
    }

    private void addNewPostToFeed(Feed feed, long postId) {
        var postIds = feed.getPostIds();
        postIds.add(postId);
        if (postIds.size() > maxCountOfPostsInFeed) {
            postIds.remove(postIds.iterator().next());
        }

        feed.setPostIds(postIds);
    }
}
