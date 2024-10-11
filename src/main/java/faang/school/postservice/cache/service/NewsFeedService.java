package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.NewsFeedRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.repository.NewsFeedRedisRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final PostRedisService postRedisService;
    private final UserRedisService userRedisService;
    private final NewsFeedRedisRepository newsFeedRedisRepository;
    private final RedisConcurrentExecutor concurrentExecutor;

    @Value("${news-feed.batch-size}")
    private int batchSize;
    @Value("${news-feed.max-size}")
    private int newsFeedMaxSize;
    @Value("${spring.data.redis.cache.news-feed.prefix}")
    private String newsFeedPrefix;
    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;

    public TreeSet<PostRedis> getNewsFeed(Long userId, Long lastPostId) {
        log.info("Getting news feed for user {}", userId);
        String key = generateKey(userId);
        List<Long> postIds = newsFeedRedisRepository.getSortedPostIds(key);
        if (postIds.isEmpty()) {
            return getPostsFromDB(userId, lastPostId, batchSize);
        }
        List<Long> resultPostIds = getResultPostIds(lastPostId, postIds);
        TreeSet<PostRedis> result = new TreeSet<>(postRedisService.getAllByIds(resultPostIds));
        if (result.size() < resultPostIds.size()) {
            addExpiredPostsAndGet(resultPostIds, result);
        }
        if (result.size() < batchSize) {
            addExtraPostsFromDB(userId, result);
        }
        setAuthors(result);
        return result;
    }

    public void addPostConcurrent(Long followerId, Long postId) {
        String key = generateKey(followerId);
        concurrentExecutor.execute(key, () -> addPost(key, postId), "adding post by id " + postId);
    }

    public void saveAllNewsFeeds(List<NewsFeedRedis> newsFeeds) {
        newsFeeds.forEach(newsFeed -> {
            String key = generateKey(newsFeed.getFollowerId());
            newsFeedRedisRepository.addAll(key, newsFeed.getPostIds());
        });
    }

    private void addPost(String key, Long postId) {
        newsFeedRedisRepository.addPostId(key, postId);

        while (newsFeedRedisRepository.getSize(key) > newsFeedMaxSize) {
            log.info("Removing excess post from {}", key);
            newsFeedRedisRepository.removeLastPostId(key);
        }
    }

    private TreeSet<PostRedis> getPostsFromDB(Long userId, Long lastPostId, int postsCount) {
        log.info("Getting posts from DB");
        List<Long> followeeIds = userServiceClient.getUser(userId).getFolloweesIds();
        List<PostRedis> posts;
        if (lastPostId == null) {
            posts = postService.findByAuthors(followeeIds, postsCount);
        } else {
            posts = postService.findByAuthorsBeforeId(followeeIds, lastPostId, postsCount);
        }
        if (posts.isEmpty()) {
            return new TreeSet<>();
        }
        setComments(posts);
        return new TreeSet<>(posts);
    }

    private void setComments(List<PostRedis> posts) {
        log.info("Setting comments for posts");
        posts.forEach(post -> {
            TreeSet<CommentRedis> comments = commentService.findLastBatchByPostId(commentsMaxSize, post.getId());
            post.setComments(comments);
        });
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

    private void addExpiredPostsAndGet(List<Long> redisPostIds, TreeSet<PostRedis> result) {
        log.info("Adding posts, that were not found in cache");

        List<Long> resultIds = result.stream()
                .map(PostRedis::getId)
                .toList();
        redisPostIds.removeAll(resultIds);

        List<PostRedis> postsRedis = postService.findAllByIdsWithLikes(redisPostIds);
        setComments(postsRedis);
        result.addAll(postsRedis);
    }

    private void addExtraPostsFromDB(Long userId, TreeSet<PostRedis> result) {
        log.info("Getting extra posts from DB for user {} because feed size is {}", userId, result.size());
        Long lastPostId = result.last().getId();
        int postsCount = batchSize - result.size();
        result.addAll(getPostsFromDB(userId, lastPostId, postsCount));
    }

    private void setAuthors(TreeSet<PostRedis> posts) {
        log.info("Setting authors to posts");
        Set<Long> userIds = findUserIds(posts);

        Map<Long, UserRedis> usersRedis = userRedisService.getAllByIds(userIds).stream()
                .collect(Collectors.toMap(UserRedis::getId, user -> user));
        if (usersRedis.size() < userIds.size()) {
            addExpiredAuthors(usersRedis, userIds);
        }
        setPostsAndCommentsAuthors(posts, usersRedis);
    }

    private Set<Long> findUserIds(TreeSet<PostRedis> posts) {
        Set<Long> userIds = new HashSet<>();
        posts.forEach(post -> {
            userIds.add(post.getAuthor().getId());
            TreeSet<CommentRedis> comments = post.getComments();
            if (comments != null) {
                comments.forEach(comment -> userIds.add(comment.getAuthor().getId()));
            }
        });
        return userIds;
    }

    private void setPostsAndCommentsAuthors(TreeSet<PostRedis> posts, Map<Long, UserRedis> usersRedis) {
        posts.forEach(post -> {
            post.setAuthor(usersRedis.get(post.getAuthor().getId()));
            TreeSet<CommentRedis> comments = post.getComments();
            if (comments != null) {
                comments.forEach(comment -> {
                    Long authorId = comment.getAuthor().getId();
                    comment.setAuthor(usersRedis.get(authorId));
                });
            }
        });
    }

    private void addExpiredAuthors(Map<Long, UserRedis> usersRedis, Set<Long> userIds) {
        log.info("Adding authors, that were not found in cache");
        List<Long> userRedisIds = usersRedis.keySet().stream().toList();
        List<Long> expiredUserIds = new ArrayList<>(userIds);
        expiredUserIds.removeAll(userRedisIds);
        List<UserDto> expiredUsers = userServiceClient.getUsersByIds(expiredUserIds);
        expiredUsers.forEach(userDto -> usersRedis.put(
                userDto.getId(), new UserRedis(userDto.getId(), userDto.getUsername())));
    }

    private String generateKey(Long userId) {
        return newsFeedPrefix + userId;
    }
}
