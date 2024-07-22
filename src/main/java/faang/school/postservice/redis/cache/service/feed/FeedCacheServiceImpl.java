package faang.school.postservice.redis.cache.service.feed;

import faang.school.postservice.redis.cache.entity.FeedCache;
import faang.school.postservice.redis.cache.entity.PostCache;
import faang.school.postservice.redis.cache.repository.FeedCacheRepository;
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
public class FeedCacheServiceImpl implements FeedCacheService {

    @Value("${spring.data.redis.cache.settings.max-feed-size}")
    private long maxFeedSize;
    private final FeedCacheRepository feedCacheRepository;
    private final RedisOperations redisOperations;

    @Override
    @Async("feedCacheTaskExecutor")
    public void addPostToFeed(PostCache post, long subscriberId) {

        FeedCache foundNewsFeed = redisOperations.findById(feedCacheRepository, subscriberId).orElse(null);

        if (foundNewsFeed == null) {

            NavigableSet<PostCache> posts = new TreeSet<>();
            posts.add(post);

            foundNewsFeed = FeedCache.builder()
                    .id(subscriberId)
                    .posts(posts)
                    .build();

            log.info("Creating new feed for user with id: {}", subscriberId);
        } else {

            NavigableSet<PostCache> currentFeed = foundNewsFeed.getPosts();
            currentFeed.add(post);
            while (currentFeed.size() > maxFeedSize) {
                currentFeed.pollLast();
            }
        }

        log.info("Adding post to feed for user with id: {}", subscriberId);

        redisOperations.updateOrSave(feedCacheRepository, foundNewsFeed, subscriberId);
    }

    @Override
    @Async("feedCacheTaskExecutor")
    public void deletePostFromFeed(PostCache post, long subscriberId) {

        FeedCache foundNewsFeed = redisOperations.findById(feedCacheRepository, subscriberId).orElse(null);

        if (foundNewsFeed != null) {

            NavigableSet<PostCache> currentFeed = foundNewsFeed.getPosts();
            currentFeed.remove(post);
            redisOperations.updateOrSave(feedCacheRepository, foundNewsFeed, subscriberId);

            log.info("Deleting post from feed for user with id: {}", subscriberId);
        }
    }

    @Override
    public FeedCache findByUserId(long userId) {
        return redisOperations.findById(feedCacheRepository, userId).orElse(null);
    }
}
