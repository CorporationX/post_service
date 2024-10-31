package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CacheableNewsFeed;
import faang.school.postservice.cache.model.CacheablePost;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.repository.CacheableNewsFeedRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CacheablePostService cacheablePostService;
    private final CacheableNewsFeedRepository cacheableNewsFeedRepository;
    private final RedisConcurrentExecutor concurrentExecutor;

    @Value("${news-feed.batch-size}")
    private int batchSize;
    @Value("${news-feed.max-size}")
    private int newsFeedMaxSize;
    @Value("${spring.data.redis.cache.news-feed.prefix}")
    private String newsFeedPrefix;

    public TreeSet<CacheablePost> getNewsFeed(Long userId, Long lastPostId) {
        log.info("Getting news feed for user {}", userId);
        String key = generateKey(userId);
        List<Long> postIds = cacheableNewsFeedRepository.getSortedPostIds(key);
        if (postIds.isEmpty()) {
            TreeSet<CacheablePost> postsFromDB = getPostsFromDB(userId, lastPostId, batchSize);
            if (postsFromDB.isEmpty()) {
                return postsFromDB;
            }
            cacheablePostService.setAuthors(postsFromDB);
            return postsFromDB;
        }
        List<Long> resultPostIds = getResultPostIds(lastPostId, postIds);
        TreeSet<CacheablePost> result = new TreeSet<>(cacheablePostService.getAllByIds(resultPostIds));
        if (result.size() < resultPostIds.size()) {
            addExpiredPosts(resultPostIds, result);
        }
        if (result.size() < batchSize) {
            addExtraPostsFromDB(userId, lastPostId,  result);
        }
        cacheablePostService.setAuthors(result);
        return result;
    }

    public void addPostConcurrent(Long followerId, Long postId) {
        String key = generateKey(followerId);
        concurrentExecutor.execute(key, () -> addPost(followerId, postId), "adding post by id " + postId);
    }

    public void addPost(Long followerId, Long postId) {
        String key = generateKey(followerId);
        cacheableNewsFeedRepository.addPostId(key, postId);

        while (cacheableNewsFeedRepository.getSize(key) > newsFeedMaxSize) {
            log.info("Removing excess post from {}", key);
            cacheableNewsFeedRepository.removeLastPostId(key);
        }
    }

    public void saveAllNewsFeeds(List<CacheableNewsFeed> newsFeeds) {
        newsFeeds.forEach(newsFeed -> {
            String key = generateKey(newsFeed.getFollowerId());
            cacheableNewsFeedRepository.addAll(key, newsFeed.getPostIds());
        });
    }

    public List<CacheableNewsFeed> getNewsFeedsForUsers(List<CacheableUser> cacheableUsers) {
        return cacheableUsers.parallelStream()
                .map(user -> {
                    List<Long> postIds = postService.findPostIdsByFollowerId(user.getId(), newsFeedMaxSize);
                    return new CacheableNewsFeed(user.getId(), postIds);
                })
                .filter(newsFeed -> !newsFeed.getPostIds().isEmpty())
                .toList();
    }

    private TreeSet<CacheablePost> getPostsFromDB(Long userId, Long lastPostId, int postsCount) {
        log.info("Getting posts from DB");
        List<Long> followeeIds = userServiceClient.getUser(userId).getFolloweesIds();
        List<CacheablePost> cacheablePosts;
        if (lastPostId == null) {
            cacheablePosts = postService.findByAuthors(followeeIds, postsCount);
        } else {
            cacheablePosts = postService.findByAuthorsBeforeId(followeeIds, lastPostId, postsCount);
        }
        if (cacheablePosts.isEmpty()) {
            return new TreeSet<>();
        }
        cacheablePostService.setCommentsFromDB(cacheablePosts);
        return new TreeSet<>(cacheablePosts);
    }

    private List<Long> getResultPostIds(Long lastPostId, List<Long> postIds) {
        if (lastPostId == null) {
            return getSubList(postIds, 0L, batchSize);
        } else {
            return getSubList(postIds, lastPostId, batchSize);
        }
    }

    private List<Long> getSubList(List<Long> postIds, long lastPostId, int batchSize) {
        int startIndex = postIds.indexOf(lastPostId) + 1;
        if (startIndex == 0 && lastPostId != 0) {
            do {
                lastPostId--;
                startIndex = postIds.indexOf(lastPostId);
                if (lastPostId == 0) {
                    startIndex = postIds.size();
                }
            } while (startIndex == -1);
        }
        int endIndex = Math.min(startIndex + batchSize, postIds.size());
        return postIds.subList(startIndex, endIndex);
    }

    private void addExpiredPosts(List<Long> cacheablePostIds, TreeSet<CacheablePost> result) {
        log.info("Adding posts, that were not found in cache");

        List<Long> resultIds = result.stream()
                .map(CacheablePost::getId)
                .toList();
        List<Long> expiredPostIds = new ArrayList<>(cacheablePostIds);
        expiredPostIds.removeAll(resultIds);

        List<CacheablePost> cacheablePosts = postService.findAllByIdsWithLikes(expiredPostIds);
        cacheablePostService.setCommentsFromDB(cacheablePosts);
        result.addAll(cacheablePosts);
    }

    private void addExtraPostsFromDB(Long userId, Long lastPostId, TreeSet<CacheablePost> result) {
        log.info("Getting extra posts from DB for user {} because feed size is {}", userId, result.size());
        if (!result.isEmpty()) {
            lastPostId = result.last().getId();
        }
        int postsCount = batchSize - result.size();
        result.addAll(getPostsFromDB(userId, lastPostId, postsCount));
    }

    private String generateKey(Long userId) {
        return newsFeedPrefix + userId;
    }
}
