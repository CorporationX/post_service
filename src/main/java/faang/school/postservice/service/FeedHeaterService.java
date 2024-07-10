package faang.school.postservice.service;

import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.FeedHeaterRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeaterService {

    private static final int NUM_THREADS = 5;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    private final FeedHeaterRepository feedHeaterRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final RedisPostRepository redisPostRepository;

    public void feedHeat() {
        log.info("Start feed heat...");

        createFeedsForUsers();
        createPostsInRedis();

        executorService.shutdown();
        log.info("Stop feed heat...");
    }

    private void createPostsInRedis() {
        log.info("Create all posts in Redis");
        log.info("Get all id posts");
        List<Long> postIds = postRepository.findAllIds();
        log.info("Post Ids: {}", postIds);
        postIds.forEach(postId ->
                CompletableFuture.runAsync(() -> createPost(postId), executorService)); // не работает.
//        postIds.forEach(this::createPost);
//        executorService.shutdown();
    }

    private void createPost(long postId) {
        log.info("Create Post with ID: {}", postId);
        log.info("Get post with ID: {} from DB", postId);
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            PostRedis postRedis = postMapper.toRedis(post.get());
            log.info("Redis post {}", postRedis);
            redisPostRepository.save(postRedis);
        }
    }

    private void createFeedsForUsers() {
        log.info("Create news feed for users in Redis");
        List<Long> userIds = feedHeaterRepository.findAllUsers();
        log.info("Users: {}", userIds);
        userIds.forEach(userId -> CompletableFuture.runAsync(() -> addFeedForUser(userId), executorService));
//        executorService.shutdown();
    }

    private void addFeedForUser(long subscriberId) {
        log.info("Find posts for user: {}", subscriberId);
        List<Long> postIds = feedHeaterRepository.findSubscriberPosts(subscriberId);
        log.info("Post ids: {} for user: {}", postIds, subscriberId);
        TreeSet<Long> treeSet = new TreeSet<>(Comparator.reverseOrder());
        treeSet.addAll(postIds);
        if (!treeSet.isEmpty()) {
            FeedRedis feedRedis = new FeedRedis(subscriberId, treeSet);
            log.info("Create news feed for user: {}", subscriberId);
            redisFeedRepository.save(feedRedis);
        }
    }
}
