package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCache;
import faang.school.postservice.dto.event.kafka.NewPostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CacheCommentMapper;
import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.CacheCommentAuthor;
import faang.school.postservice.model.CacheUser;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.RedisAuthorCommentRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final RedisAuthorCommentRepository redisAuthorCommentRepository;
    private final PostService postService;
    private final CachePostMapper cachePostMapper;
    private final CacheCommentMapper cacheCommentMapper;

    @Value("${spring.feed.max-size}")
    private int maxFeedSize;
    @Value("${spring.feed.max-size-batch}")
    private int sizeBatchPostToFeed;
    @Value("${spring.author-comment.ttl}")
    private int authorCommentTtl;
    @Value("${spring.post.cache.max-comment}")
    private int maxCommentToCachePost;

    public void addPostToFollowers(NewPostEvent newPostEvent) {
        List<CacheUser> users = StreamSupport.stream(
                        redisUserRepository.findAllById(newPostEvent.getSubscribersIds()).spliterator(), false)
                .toList();

        users.forEach(user -> {
            LinkedHashSet<Long> postIds = user.getFeed();
            postIds.add(newPostEvent.getId());

            if (postIds.size() > maxFeedSize) {
                removeLastItem(postIds);
            }
        });
        redisUserRepository.saveAll(users);
    }

    public List<CachePost> getFeed(Long lastPostId) {
        PageRequest pageRequest;
        if (lastPostId == null) {
            pageRequest = PageRequest.of(0, sizeBatchPostToFeed);
        } else {
            pageRequest = PageRequest.of((int) (lastPostId / sizeBatchPostToFeed) + 1, sizeBatchPostToFeed);
        }

        CacheUser user = redisUserRepository.findById(userContext.getUserId())
                .orElseThrow(() -> {
                    String errorMsg = String.format("User id: %d not found", userContext.getUserId());
                    log.error(errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });

        return fillBatch(user.getId(), new ArrayList<>(user.getFeed()), pageRequest);
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public List<CachePost> fillBatch(long userId, List<Long> postIds, PageRequest pageRequest) {
        List<CachePost> cachePosts = new ArrayList<>();
        redisPostRepository.findAllById(postIds, pageRequest).forEach(cachePosts::add);

        if (cachePosts.size() < maxFeedSize) {
            List<Post> posts = postService.findPostsByAuthorIds(userServiceClient.getFollowingIds(userId),
                    PageRequest.of(pageRequest.getPageNumber(), maxFeedSize - postIds.size()));

            cachePosts.addAll(cachePostMapper.convertPostsToCachePosts(posts));
        }
        return cachePosts;
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void addLikeToPost(long postId) {
        CachePost cachePost = getCachePost(postId);

        cachePost.incrementLike();
        cachePost.incrementVersion();
        redisPostRepository.save(cachePost);
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void addCommentToPost(long postId, CommentCache commentCache) {
        CachePost cachePost = getCachePost(postId);
        if (cachePost.getComments() == null) {
            cachePost.setComments(new LinkedHashSet<>());
        }
        LinkedHashSet<CommentCache> comments = cachePost.getComments();

        comments.add(commentCache);
        if (cachePost.getComments().size() > maxCommentToCachePost) {
            removeLastItem(comments);
        }

        cachePost.incrementVersion();
        redisPostRepository.save(cachePost);
        saveAuthorComment(commentCache.getAuthorId());
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void addViewToPost(long postId) {
        CachePost cachePost = getCachePost(postId);

        cachePost.incrementView();
        cachePost.incrementVersion();
        redisPostRepository.save(cachePost);
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void saveAuthorComment(long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        redisAuthorCommentRepository.save(CacheCommentAuthor.builder()
                .id(authorId)
                .userName(user.getUsername())
                .ttl(authorCommentTtl)
                .build());
    }

    public CachePost getCachePost(long postId) {
        return redisPostRepository.findById(postId).orElseGet(() -> {
            Post post = postService.findPostByIdWithLikes(postId);
            List<Comment> comments = commentRepository.findByPostIdToCache(
                    postId, PageRequest.of(0, maxCommentToCachePost));

            List<CommentCache> commentCaches = cacheCommentMapper.convertCommentsToCacheComments(comments);
            CachePost cachePost = cachePostMapper.converPostToCachePost(post, commentCaches);

            redisPostRepository.save(cachePost);
            return cachePost;
        });
    }

    private void removeLastItem(LinkedHashSet<?> items) {
        Iterator<?> it = items.iterator();
        if (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}
