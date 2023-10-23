package faang.school.postservice.repository;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisPostRepository {
    private final RedisTemplate<Long, PostCacheDto> redisTemplate;

    public void save(long postId, PostCacheDto postCacheDto) {
        redisTemplate.opsForValue().set(postId, postCacheDto);
        redisTemplate.expire(postId, 1, TimeUnit.DAYS);
        log.info("Post was successfully saved {}", postCacheDto);
    }

    @Cacheable(value = "postCache", key = "#postId")
    public PostCacheDto getPostById(long postId) {
        PostCacheDto post = redisTemplate.opsForValue().get(postId);

        if (post != null) {
            log.warn("User with ID {} found in cache", postId);
        } else {
            log.warn("User with ID {} not found in cache", postId);
        }
        return post;
    }

    public void increaseLikeCounter(long postId) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) {
                operations.watch(postId);
                PostCacheDto post = redisTemplate.opsForValue().get(postId);

                if (post != null) {
                    long updatedLikeCounter = post.getLikeCounter() + 1;
                    post.setLikeCounter(updatedLikeCounter);

                    redisTemplate.opsForValue().set(postId, post);
                    operations.unwatch();
                }
                return null;
            }
        });
    }

    public void addComment(long postId, CommentDto comment) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) {
                operations.watch(postId);
                PostCacheDto post = redisTemplate.opsForValue().get(postId);

                if (post.getComments().size() >= 3) {
                    PostCacheDto updatedPost = new PostCacheDto();
                    updatedPost.setPostId(post.getPostId());
                    updatedPost.setComments(new LinkedHashSet<>(post.getComments()));
                    updatedPost.getComments().add(comment);

                    redisTemplate.opsForValue().setIfPresent(postId, updatedPost);
                    operations.unwatch();
                }

                return null;
            }
        });
    }

    public void increasePostView(long postId) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) {
                operations.watch(postId);
                PostCacheDto post = redisTemplate.opsForValue().get(postId);

                if (post != null) {
                    long currentViews = post.getViews();

                    long updatedViews = currentViews + 1;
                    post.setViews(updatedViews);

                    if (currentViews == post.getViews() && redisTemplate.opsForValue().setIfPresent(postId, post)) {
                        operations.unwatch();
                    }
                }
                return null;
            }
        });
    }
}
