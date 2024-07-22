package faang.school.postservice.redis.cache.service.feed;

import faang.school.postservice.redis.cache.entity.FeedRedisCache;
import faang.school.postservice.redis.cache.entity.PostRedisCache;
import faang.school.postservice.redis.cache.repository.FeedRedisRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.NavigableSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedRedisCacheServiceImpl implements FeedRedisCacheService {

    @Value("${spring.data.redis.cache.settings.max-feed-size}")
    private long maxFeedSize;
    private final FeedRedisRepository feedRedisRepository;
    private final RedisOperations redisOperations;

    @Override
    @Async("feedCacheTaskExecutor")
    public void addPostToFeed(PostRedisCache post, long subscriberId) {

        FeedRedisCache foundNewsFeed = redisOperations.findById(feedRedisRepository, subscriberId).orElse(null);

        if (foundNewsFeed == null) {

            NavigableSet<PostRedisCache> posts = new TreeSet<>();
            posts.add(post);

            foundNewsFeed = FeedRedisCache.builder()
                    .id(subscriberId)
                    .posts(posts)
                    .build();

            log.info("Creating new feed for user with id: {}", subscriberId);
        } else {

            NavigableSet<PostRedisCache> currentFeed = foundNewsFeed.getPosts();
            currentFeed.add(post);
            while (currentFeed.size() > maxFeedSize) {
                currentFeed.pollLast();
            }
        }

        log.info("Adding post to feed for user with id: {}", subscriberId);

        redisOperations.updateOrSave(feedRedisRepository, foundNewsFeed, subscriberId);
    }

    @Override
    @Async("feedCacheTaskExecutor")
    public void deletePostFromFeed(PostRedisCache post, long subscriberId) {

        FeedRedisCache foundNewsFeed = redisOperations.findById(feedRedisRepository, subscriberId).orElse(null);

        if (foundNewsFeed != null) {

            NavigableSet<PostRedisCache> currentFeed = foundNewsFeed.getPosts();
            currentFeed.remove(post);
            redisOperations.updateOrSave(feedRedisRepository, foundNewsFeed, subscriberId);

            log.info("Deleting post from feed for user with id: {}", subscriberId);
        }
    }

    @Override
    public FeedRedisCache findByUserId(long userId) {
        return redisOperations.findById(feedRedisRepository, userId).orElse(null);
    }
}
