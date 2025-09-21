package faang.school.postservice.service.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.CacheWarmupTask;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.factory.UserCacheFactory;
import faang.school.postservice.factory.post.FeedPostFactory;
import faang.school.postservice.factory.post.PostCacheFactory;
import faang.school.postservice.kafka.producer.feed.CacheWarmupProducer;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.repository.CacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.SubscriptionRepository;
import faang.school.postservice.repository.redis.feed.FeedCacheRepository;
import faang.school.postservice.repository.redis.post.PostCacheRepository;
import faang.school.postservice.repository.redis.user.UserCacheRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final FeedCacheRepository feedCacheRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostRepository postRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostCacheFactory postCacheFactory;
    private final FeedPostFactory feedPostFactory;
    private final CacheWarmupProducer cacheWarmupProducer;
    private final CacheRepository cacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final UserCacheFactory userCacheFactory;
    private final UserContext userContext;

    @Value("${cache.feed.warm-up-batch-size}")
    private int warmUpBatchSize;

    @Value("${cache.feed.max-records}")
    private int feedSize;

    @Value("${feed.page-size}")
    private int feedPageSize;

    public void updateUserFeeds(PostPublishedEvent postPublishedEvent) {
        Post post = postRepository.findById(postPublishedEvent.getPostId()).orElseThrow(
                () -> new IllegalStateException("Published unknown post id: "
                        + postPublishedEvent.getPostId())
        );
        for (Long subscriberId : postPublishedEvent.getSubscribers()) {
            feedCacheRepository.addPostToFeed(subscriberId, post);
        }
        updateUserAndPostCaches(List.of(post));
    }

    public void queueCacheWarmUp() {
        log.info("Starting feed cache warmup.");
        List<Long> allSubscriberIds = subscriptionRepository.getAllFollowerIds();
        for (int i = 0; i < allSubscriberIds.size(); i += warmUpBatchSize) {
            List<Long> batch = allSubscriberIds.subList(
                    i,
                    Math.min(i + warmUpBatchSize, allSubscriberIds.size())
            );
            cacheWarmupProducer.publishCacheWarmupTaskEvent(new CacheWarmupTask(batch));
        }
    }

    public void warmUp(List<Long> subscriberIds) {
        for (long userId : subscriberIds) {
            log.info("Building feed for user ID: {}", userId);
            List<Post> posts = postRepository.getFeedForUser(userId, feedSize, null);
            feedCacheRepository.addPostsBatched(userId, posts);
            updateUserAndPostCaches(posts);
        }
    }

    public List<FeedPostDto> getFeed(Long lastPostId) {
        List<Long> feedPostIds = feedCacheRepository.getFeedAfter(userContext.getUserId(), lastPostId, feedPageSize);
        List<FeedPostDto> feed = new ArrayList<>();

        for (long postId : feedPostIds) {
            feed.add(getFeedPost(postId));
        }

        return maybeExtendFeed(lastPostId, feed);
    }

    private FeedPostDto getFeedPost(long postId) {
        Optional<PostCache> cachePost = postCacheRepository.findById(String.valueOf(postId));
        if (cachePost.isPresent()) {
            return feedPostFactory.fromPostCache(cachePost.get());
        }

        log.info("Post ID: {} cache miss.", postId);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalStateException("Received unknown post ID: " + postId)
        );

        return feedPostFactory.fromPost(post);
    }

    private List<FeedPostDto> maybeExtendFeed(Long lastPostId, List<FeedPostDto> feed) {
        if (feed.size() >= feedPageSize) {
            return feed;
        }

        Long anchorId = feed.isEmpty()
                ? lastPostId
                : feed.get(feed.size() - 1).getId();

        log.info("End of cached feed for user {}. Checking DB after anchor {}.",
                userContext.getUserId(), anchorId);

        List<Post> morePosts = postRepository.getFeedForUser(
                userContext.getUserId(),
                feedPageSize - feed.size(),
                anchorId
        );

        if (!morePosts.isEmpty()) {
            log.info(
                    "Found additional {} posts to show for user {}.",
                    morePosts.size(),
                    userContext.getUserId()
            );
        }

        for (Post p : morePosts) {
            feed.add(feedPostFactory.fromPost(p));
        }
        return feed;
    }

    private void updateUserAndPostCaches(@NonNull List<Post> posts) {
        HashSet<Long> authorIds = new HashSet<>();
        for (Post post : posts) {
            authorIds.add(post.getAuthorId());
            postCacheRepository.saveIfAbsent(postCacheFactory.from(post));
        }

        List<UserDto> users = cacheRepository.getUsers(authorIds.stream().toList());
        for (UserDto userDto : users) {
            UserCache userCache = userCacheFactory.from(userDto);
            boolean cached = userCacheRepository.saveIfAbsent(userCache);
            if (cached) {
                log.info("UserCache added for ID: {}", userCache.getId());
            } else {
                log.info("UserCache already exists for ID: {}", userCache.getId());
            }
        }

    }
}
