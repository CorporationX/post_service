package faang.school.postservice.redis.service.impl;

import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.model.event.kafka.PostEventKafka;
import faang.school.postservice.redis.mapper.CommentRedisMapper;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.dto.CommentRedisDto;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.FeedCacheService;
import faang.school.postservice.redis.service.PostCacheService;
import faang.school.postservice.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${cache.post-ttl}")
    private long postTtl;

    @Value("${cache.post.fields.views}")
    private String postCacheViewsField;

    @Value("${cache.post.fields.number-of-likes}")
    private String numberOfLikesField;

    @Value("${cache.post.prefix}")
    private String cachePrefix;

    @Value("${post-comments.size}")
    private int postCommentsSize;

    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheMapper postCacheMapper;
    private final RedissonClient redissonClient;
    private final FeedCacheService feedCacheService;
    private final CommentRepository commentRepository;
    private final CommentRedisMapper commentRedisMapper;

    @Autowired
    public PostCacheServiceImpl(PostCacheRedisRepository postCacheRedisRepository,
                                @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate,
                                PostCacheMapper postCacheMapper, RedissonClient redissonClient,
                                FeedCacheService feedCacheService, CommentRepository commentRepository,
                                CommentRedisMapper commentRedisMapper) {
        this.postCacheRedisRepository = postCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.postCacheMapper = postCacheMapper;
        this.redissonClient = redissonClient;
        this.feedCacheService = feedCacheService;
        this.commentRepository = commentRepository;
        this.commentRedisMapper = commentRedisMapper;
    }

    @Override
    public void savePostToCache(PostDto post) {
        log.info("Saving post with ID {} to cache", post.getId());

        PostCache postCache = postCacheMapper.toPostCache(post);
        postCacheRedisRepository.save(postCache);

        String key = createPostCacheKey(post.getId());

        redisTemplate.expire(key, Duration.ofSeconds(postTtl));

        log.info("Post with ID {} saved to cache with key: {} and TTL: {} seconds", post.getId(), key, postTtl);
    }

    @Override
    public void addPostView(PostDto post) {
        if (!postCacheRedisRepository.existsById(post.getId())) {
            throw new NoSuchElementException("Can't find post in redis with id: " + post.getId());
        }

        String lockKey = "lock:" + post.getId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            log.debug("Lock acquired for postId: {}", post.getId());
            incrementNumberOfPostViews(post.getId());
            log.info("Successfully incremented views for postId: {}", post.getId());
        } finally {
            lock.unlock();
            log.debug("Lock released for postId: {}", post.getId());
        }
    }

    @Override
    public void addPostLike(LikeDto like) {
        if (!postCacheRedisRepository.existsById(like.getPostId())) {
            throw new NoSuchElementException("Can't find post in redis with id: " + like.getPostId());
        }

        String lockKey = "lock:" + like.getPostId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            log.debug("Lock acquired for postId: {}", like.getPostId());
            incrementNumberOfPostLikes(like.getPostId());
            log.info("Successfully incremented likes for postId: {}", like.getPostId());
            updatePostLikes(like);
            log.info("Successfully updated likes for postId: {}", like.getPostId());

        } finally {
            lock.unlock();
            log.debug("Lock released for postId: {}", like.getPostId());
        }
    }

    private void incrementNumberOfPostViews(Long postId) {
        redisTemplate.opsForHash()
                .increment(createPostCacheKey(postId), String.valueOf(postCacheViewsField), 1);
    }

    private void incrementNumberOfPostLikes(Long postId) {
        redisTemplate.opsForHash()
                .increment(createPostCacheKey(postId), String.valueOf(numberOfLikesField), 1);
    }

    private String createPostCacheKey(Long postId) {
        return cachePrefix + postId;
    }

    @Override
    public void updatePostComments(CommentEventKafka event) {
        PostCache postCache = postCacheRedisRepository.findById(event.getPostId())
                .orElseThrow(() -> new NoSuchElementException("Can't find post in redis with id: " + event.getPostId()));

        log.info("Starting processing of CommentEventKafka for Post ID: {}", event.getPostId());

        String lockKey = "lock:" + event.getPostId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();

        try {
            TreeSet<CommentRedisDto> postComments = postCache.getComments();
            CommentRedisDto commentRedisDto = CommentRedisDto.builder()
                    .id(event.getCommentId())
                    .postId(event.getPostId()).content(event.getContent())
                    .createdAt(event.getCreatedAt()).authorId(event.getAuthorId()).build();
            if (postComments == null) {
                postComments = new TreeSet<>();
            } else if (postComments.size() == postCommentsSize) {
                postComments.remove(postComments.last());
            }
            postComments.add(commentRedisDto);
            postCache.setComments(postComments);
            postCacheRedisRepository.save(postCache);
        } finally {
            lock.unlock();
            log.info("Successfully processed CommentEventKafka for Post ID: {}", event.getPostId());

        }
    }

    @Override
    public void updateFeedsInCache(PostEventKafka event) {
        log.info("Starting processing of PostEventKafka for Post ID: {}", event.getPostId());
        List<CompletableFuture<Void>> features = event.getFollowerIds().stream()
                .map(followerId -> feedCacheService.getAndSaveFeed(followerId, event.getPostId()))
                .toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(features.toArray(new CompletableFuture[0]));
        allFutures.join();
        log.info("Successfully processed PostEventKafka for Post ID: {}", event.getPostId());
    }

    @Override
    public CompletableFuture<Void> saveAllPostsToCache(List<PostDto> posts) {

        List<PostCache> newPostCaches = filterNewPosts(posts).stream()
                .map(post -> {
                    PostCache postCache = postCacheMapper.toPostCache(post);
                    TreeSet<CommentRedisDto> lastThreeComments = getLastThreeComments(post.getId());
                    postCache.setComments(lastThreeComments);
                    return postCache;
                })
                .toList();

        if (!newPostCaches.isEmpty()) {
            log.info("Saving {} new posts to cache.", newPostCaches.size());
            return CompletableFuture.runAsync(() -> {
                postCacheRedisRepository.saveAll(newPostCaches);
                setTtlForPosts(newPostCaches);
            });
        }

        log.info("No new posts to cache.");
        return CompletableFuture.completedFuture(null);
    }

    private List<PostDto> filterNewPosts(List<PostDto> posts) {
        List<String> keys = posts.stream()
                .map(post -> "post:" + post.getId())
                .toList();

        List<Object> results = redisTemplate.opsForValue().multiGet(keys);

        if (results == null) {
            log.warn("Failed to retrieve keys from Redis. Assuming all posts are new.");
            return posts;
        }

        return posts.stream()
                .filter(user -> {
                    int index = posts.indexOf(user);
                    return results.get(index) == null;
                })
                .toList();
    }

    private void setTtlForPosts(List<PostCache> newPostCaches) {
        newPostCaches.forEach(postCache -> {
            String key = createPostCacheKey(postCache.getId());
            redisTemplate.expire(key, Duration.ofSeconds(postTtl));
            log.info("Set TTL for post {} in cache.", postCache.getId());
        });
    }

    private void updatePostLikes(LikeDto like) {
        PostCache postCache = postCacheRedisRepository.findById(like.getPostId())
                .orElseThrow(() -> new NoSuchElementException("Can't find post in redis with id: " + like.getPostId()));

        List<LikeDto> postLikes = postCache.getLikes();
        if (postLikes == null) {
            postLikes = new ArrayList<>();
        }
        postLikes.add(like);
        postCache.setLikes(postLikes);
        postCacheRedisRepository.save(postCache);
    }

    private TreeSet<CommentRedisDto> getLastThreeComments(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .limit(3)
                .map(commentRedisMapper::toCommentRedisDto)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
