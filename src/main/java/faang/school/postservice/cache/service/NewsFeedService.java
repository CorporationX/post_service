package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.NewsFeedRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.repository.NewsFeedRedisRepository;
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
    private final PostRedisService postRedisService;
    private final NewsFeedRedisRepository newsFeedRedisRepository;
    private final RedisConcurrentExecutor concurrentExecutor;

    @Value("${news-feed.batch-size}")
    private int batchSize;
    @Value("${news-feed.max-size}")
    private int newsFeedMaxSize;
    @Value("${spring.data.redis.cache.news-feed.prefix}")
    private String newsFeedPrefix;

    public TreeSet<PostRedis> getNewsFeed(Long userId, Long lastPostId) {
        log.info("Getting news feed for user {}", userId);
        String key = generateKey(userId);
        List<Long> postIds = newsFeedRedisRepository.getSortedPostIds(key);
        if (postIds.isEmpty()) {
            TreeSet<PostRedis> postsFromDB = getPostsFromDB(userId, lastPostId, batchSize);
            if (postsFromDB.isEmpty()) {
                return postsFromDB;
            }
            postRedisService.setAuthors(postsFromDB);
            return postsFromDB;
        }
        List<Long> resultPostIds = getResultPostIds(lastPostId, postIds);
        TreeSet<PostRedis> result = new TreeSet<>(postRedisService.getAllByIds(resultPostIds));
        if (result.size() < resultPostIds.size()) {
            addExpiredPosts(resultPostIds, result);
        }
        if (result.size() < batchSize) {
            addExtraPostsFromDB(userId, result);
        }
        postRedisService.setAuthors(result);
        return result;
    }

    public void addPostConcurrent(Long followerId, Long postId) {
        String key = generateKey(followerId);
        concurrentExecutor.execute(key, () -> addPost(followerId, postId), "adding post by id " + postId);
    }

    public void addPost(Long followerId, Long postId) {
        String key = generateKey(followerId);
        newsFeedRedisRepository.addPostId(key, postId);

        while (newsFeedRedisRepository.getSize(key) > newsFeedMaxSize) {
            log.info("Removing excess post from {}", key);
            newsFeedRedisRepository.removeLastPostId(key);
        }
    }

    public void saveAllNewsFeeds(List<NewsFeedRedis> newsFeeds) {
        newsFeeds.forEach(newsFeed -> {
            String key = generateKey(newsFeed.getFollowerId());
            newsFeedRedisRepository.addAll(key, newsFeed.getPostIds());
        });
    }

    public List<NewsFeedRedis> getNewsFeedsForUsers(List<UserRedis> usersRedis) {
        return usersRedis.parallelStream()
                .map(user -> {
                    List<Long> postIds = postService.findPostIdsByFollowerId(user.getId(), newsFeedMaxSize);
                    return new NewsFeedRedis(user.getId(), postIds);
                })
                .filter(newsFeed -> !newsFeed.getPostIds().isEmpty())
                .toList();
    }

    private TreeSet<PostRedis> getPostsFromDB(Long userId, Long lastPostId, int postsCount) {
        log.info("Getting posts from DB");
        List<Long> followeeIds = userServiceClient.getUser(userId).getFolloweesIds();
        List<PostRedis> postsRedis;
        if (lastPostId == null) {
            postsRedis = postService.findByAuthors(followeeIds, postsCount);
        } else {
            postsRedis = postService.findByAuthorsBeforeId(followeeIds, lastPostId, postsCount);
        }
        if (postsRedis.isEmpty()) {
            return new TreeSet<>();
        }
        postRedisService.setCommentsFromDB(postsRedis);
        return new TreeSet<>(postsRedis);
    }

    private List<Long> getResultPostIds(Long lastPostId, List<Long> postIds) {
        if (lastPostId == null) {
            return getSubList(postIds, 0L, batchSize);
        } else {
            return getSubList(postIds, lastPostId, batchSize);
        }
    }

    private List<Long> getSubList(List<Long> list, long lastPostId, int batchSize) {
        int startIndex = list.indexOf(lastPostId) + 1;
        int endIndex = Math.min(startIndex + batchSize, list.size());
        return list.subList(startIndex, endIndex);
    }

    private void addExpiredPosts(List<Long> redisPostIds, TreeSet<PostRedis> result) {
        log.info("Adding posts, that were not found in cache");

        List<Long> resultIds = result.stream()
                .map(PostRedis::getId)
                .toList();
        List<Long> expiredPostIds = new ArrayList<>(redisPostIds);
        expiredPostIds.removeAll(resultIds);

        List<PostRedis> postsRedis = postService.findAllByIdsWithLikes(expiredPostIds);
        postRedisService.setCommentsFromDB(postsRedis);
        result.addAll(postsRedis);
    }

    private void addExtraPostsFromDB(Long userId, TreeSet<PostRedis> result) {
        log.info("Getting extra posts from DB for user {} because feed size is {}", userId, result.size());
        Long lastPostId = result.last().getId();
        int postsCount = batchSize - result.size();
        result.addAll(getPostsFromDB(userId, lastPostId, postsCount));
    }

    private String generateKey(Long userId) {
        return newsFeedPrefix + userId;
    }
}
