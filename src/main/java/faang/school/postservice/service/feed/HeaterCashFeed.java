package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostForFeedHeater;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.FeedHeaterRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserJdbcRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class HeaterCashFeed {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final FeedHeaterRepository feedHeaterRepository;
    private final UserJdbcRepository userJdbcRepository;

    @Value("${spring.heater.capacity.max}")
    private int maxSizePosts;

    public void feedHeat() {
        log.info("Start feed heat...");

        createFeedsForUsers();
        List<PostForFeedHeater> posts = postRepository.findAllPublishedPostsForFeedHeater();
        createAuthorsInRedis(posts.stream().map(PostForFeedHeater::getAuthorId).toList());
        createPostsInRedis(posts.stream().map(PostForFeedHeater::getId).toList());

        EXECUTOR_SERVICE.shutdown();
    }

    private void createFeedsForUsers() {
        log.info("Create news feed for users in Redis");

        List<Long> usersIds = feedHeaterRepository.findAllUsers();
        usersIds.forEach(usersId -> CompletableFuture.runAsync(() -> addFeedForUser(usersId), EXECUTOR_SERVICE));
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

    private void createAuthorsInRedis(List<Long> users) {
        log.info("Create all Authors in Redis");

        users.forEach(userId ->
                CompletableFuture.runAsync(() -> createAuthor(userId), EXECUTOR_SERVICE));
    }

    public void createAuthor(long authorId) {
        log.info("Create user with ID: {}", authorId);

        UserRedis userRedis = userJdbcRepository.findUserById(authorId);
        if (userRedis != null) {
            redisUserRepository.save(userRedis);
            log.info("Author with ID: {} was save", authorId);
        } else {
            log.info("Author with ID: {} not found", authorId);
        }
    }

    private void createPostsInRedis(List<Long> posts) {
        log.info("Create all posts in Redis");

        posts.forEach(postId ->
                CompletableFuture.runAsync(() -> createPost(postId), EXECUTOR_SERVICE));
    }

    public void createPost(long postId) {
        log.info("Create Post with ID: {}", postId);

        postRepository.findById(postId).ifPresentOrElse(post -> {
            PostRedis postRedis = postMapper.toPostRedis(post);
            redisPostRepository.save(postRedis);
        }, () -> log.info("Post with ID: {} not found", postId));
    }
}
