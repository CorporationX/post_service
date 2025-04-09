package faang.school.postservice.redis.service.post;

import faang.school.postservice.dto.comment.CommentRedisDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.mapper.comment.CommentRedisMapper;
import faang.school.postservice.mapper.post.PostCacheMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.repository.PostRedisRepository;
import faang.school.postservice.redis.service.feed.FeedCacheService;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${cache.post-ttl}")
    private final long postTtl;

    @Value("${cache.post.fields.views}")
    private final String postCacheViewsField;

    @Value("${cache.post.fields.number-of-likes}")
    private final String numberOfLikesField;

    @Value("${cache.post.prefix}")
    private final String cachePrefix;

    @Value("${post-comments.size}")
    private final int postCommentsSize;

    private final PostCacheRedisRepository postCacheRedisRepository;
    private final PostCacheMapper postCacheMapper;
    private final FeedCacheService feedCacheService;
    private final CommentRepository commentRepository;
    private final CommentRedisMapper commentRedisMapper;
    private final PostRedisRepository postRedisRepository;


    @Override
    public void savePostToCache(PostCacheDto post) {
        log.info("Saving post with ID {} to cache", post.getId());

        PostCache postCache = postCacheMapper.toPostCache(post);
        postCacheRedisRepository.save(postCache);
        postRedisRepository.savePost(postCache, postTtl, cachePrefix);
    }

    @Override
    public void addPostView(PostCacheDto post) {

        String lockKey = "lock:" + post.getId();
        RLock lock = postRedisRepository.acquireLock(lockKey);
        lock.lock();
        try {
            postRedisRepository.incrementHashValue(createPostCacheKey(post.getId()), postCacheViewsField);
            log.info("Successfully incremented views for postId: {}", post.getId());
        } finally {
            lock.unlock();
            log.debug("Lock released for postId: {}", post.getId());
        }
    }

    @Override
    public void updateFeedsInCache(PostCreatedEvent event) {

        List<CompletableFuture<Void>> features = event.getSubscriberIds().stream()
                .map(followerId -> feedCacheService.getAndSaveFeed(followerId, event.getPostId()))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(features.toArray(new CompletableFuture[0]));
        allFutures.join();
    }

    @Override
    public CompletableFuture<Void> saveAllPostsToCache(List<PostCacheDto> posts) {

        List<PostCache> newPostCaches = filterNewPosts(posts).stream()
                .map(post -> {
                    PostCache postCache = postCacheMapper.toPostCache(post);
                    postCache.setComments(getLastThreeComments(post.getId()));
                    return postCache;
                }).toList();

        if (!newPostCaches.isEmpty()) {
            log.info("Saving {} new posts to cache.", newPostCaches.size());
            return CompletableFuture.runAsync(() -> {
                postCacheRedisRepository.saveAll(newPostCaches);
                postRedisRepository.saveAll(newPostCaches, postTtl, cachePrefix);
            });
        }
        log.info("No new posts to cache.");
        return CompletableFuture.completedFuture(null);
    }

    private String createPostCacheKey(Long postId) {
        return cachePrefix + postId;
    }

    private List<PostCacheDto> filterNewPosts(List<PostCacheDto> posts) {

        List<String> keys = posts.stream()
                .map(post -> createPostCacheKey(post.getId()))
                .toList();

        List<Object> results = postRedisRepository.getPostsByKeys(keys);

        return posts.stream()
                .filter(post -> results.get(posts.indexOf(post)) == null)
                .toList();
    }

    private TreeSet<CommentRedisDto> getLastThreeComments(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .limit(postCommentsSize)
                .map(commentRedisMapper::toCommentRedisDto)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}