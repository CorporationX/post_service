package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.kafka.NewPostEvent;
import faang.school.postservice.model.CacheUser;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
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
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    @Value("${spring.feed.max-size}")
    private int maxFeedSize;
    @Value("${spring.feed.max-size-batch}")
    private int sizeBatchPostToFeed;
    @Value("${spring.post.cache.ttl}")
    private int postTtl;

    public void addPostToFollowers(NewPostEvent newPostEvent) {
        List<CacheUser> users = StreamSupport.stream(
                        redisUserRepository.findAllById(newPostEvent.getSubscribersIds()).spliterator(), false)
                .toList();

        users.forEach(user -> {
            LinkedHashSet<Long> postIds = user.getFeed();
            postIds.add(newPostEvent.getId());

            if (postIds.size() > maxFeedSize) {
                Iterator<Long> it = postIds.iterator();
                if (it.hasNext()) {
                    it.next();
                    it.remove();
                }
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

    public List<CachePost> fillBatch(long userId, List<Long> postIds, PageRequest pageRequest) {
        List<CachePost> posts = StreamSupport.stream(
                        redisPostRepository.findAllById(postIds, pageRequest).spliterator(), false)
                .toList();

        if (posts.size() < maxFeedSize) {
            postRepository.findPostsByAuthorIds(
                    userServiceClient.getFollowingIds(userId),
                    PageRequest.of(pageRequest.getPageNumber(), maxFeedSize - postIds.size()));
        }
        return posts;
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void addLikeToPost(long postId) {
        CachePost cachePost = redisPostRepository.findById(postId).orElseGet(() -> {
            Post post = postRepository.findByIdWithLikes(postId).orElseThrow(() -> {
                String errorMsg = String.format("Post id: %d not found", postId);
                log.error(errorMsg);
                return new EntityNotFoundException(errorMsg);
            });
            List<Comment> comments = commentRepository.findByPostId(postId);

            CachePost newCachePost = CachePost.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .countLike(post.getLikes().size())
                    .firstComment(comments.isEmpty() ? null : post.getComments().get(0))
                    .ttl(postTtl)
                    .build();

            redisPostRepository.save(newCachePost);
            return newCachePost;
        });

        cachePost.incrementLike();
        cachePost.incrementVersion();
        redisPostRepository.save(cachePost);
    }
}
