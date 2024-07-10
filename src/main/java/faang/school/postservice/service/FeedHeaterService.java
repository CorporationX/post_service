package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostForFeedHeater;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.FeedHeaterRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import faang.school.postservice.repository.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeaterService {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private final FeedHeaterRepository feedHeaterRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final RedisPostRepository redisPostRepository;
    private final PostService postService;
    private final UserJdbcRepository userJdbcRepository;
    private final RedisUserRepository redisUserRepository;

    public void feedHeat() {
        log.info("Start feed heat...");

        createFeedsForUsers();
        List<PostForFeedHeater> posts = postRepository.findAllWithIdAndAuthorId();
        createAuthorsInRedis(posts.stream().map(PostForFeedHeater::getAuthorId).toList());
        createPostsInRedis(posts.stream().map(PostForFeedHeater::getId).toList());

        executorService.shutdown();
    }

    private void createAuthorsInRedis(List<Long> users) {
        log.info("Create all Authors in Redis");
        users.forEach(userId ->
                CompletableFuture.runAsync(() -> createAuthor(userId), executorService));
    }

    public void createAuthor(long authorId) {
        log.info("Create user with ID: {}", authorId);
        UserRedis userRedis = userJdbcRepository.findUserById(authorId);
        if (userRedis != null) {
            redisUserRepository.save(userRedis);
        } else {
            log.info("Author with ID: {} not found", authorId);
        }
    }

    private void createPostsInRedis(List<Long> posts) {
        log.info("Create all posts in Redis");
        posts.forEach(postId ->
                CompletableFuture.runAsync(() -> createPost(postId), executorService));
    }

    public void createPost(long postId) {
        log.info("Create Post with ID: {}", postId);
        Post post = postService.findPostWithCommentsAndLikes(postId);
        if (post != null) {
            redisPostRepository.save(postMapper.toRedis(post));
        } else {
            log.info("Post with ID: {} not found", postId);
        }
    }

    private void createFeedsForUsers() {
        log.info("Create news feed for users in Redis");
        List<Long> userIds = feedHeaterRepository.findAllUsers();
        userIds.forEach(userId -> CompletableFuture.runAsync(() -> addFeedForUser(userId), executorService));
    }

    public void addFeedForUser(long subscriberId) {
        log.info("Find posts for user: {}", subscriberId);
        List<Long> postIds = feedHeaterRepository.findSubscriberPosts(subscriberId);
        TreeSet<Long> treeSet = new TreeSet<>(Comparator.reverseOrder());
        treeSet.addAll(postIds);
        if (!treeSet.isEmpty()) {
            FeedRedis feedRedis = new FeedRedis(subscriberId, treeSet);
            log.info("Create news feed for user: {}", subscriberId);
            redisFeedRepository.save(feedRedis);
        } else {
            log.info("User with ID: {} has not any subscriptions", subscriberId);
        }
    }
}
