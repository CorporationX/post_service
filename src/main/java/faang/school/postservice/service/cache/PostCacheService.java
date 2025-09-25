package faang.school.postservice.service.cache;

import faang.school.postservice.config.props.FeedProps;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private static final String KEY_SET_PATTERN = "%s:%d:%s";
    private final Lock likeLock = new ReentrantLock();
    private final Lock viewLock = new ReentrantLock();

    private final PostRedisRepository postRedisRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final PostMapper postMapper;
    private final JsonMapper jsonMapper;
    private final FeedProps feedProps;

    public void like(long postId) {
        likeLock.lock();
        try {
            postRedisRepository.findById(postId).ifPresentOrElse(
                    post -> {
                        post.setLikes(post.getLikes() + 1);
                        postRedisRepository.save(post);
                        log.info("Liked post with id = {} in cache", postId);
                    },
                    () -> log.warn("Like didn't add to post. Cause: post not found by id {} in cache", postId));
        } finally {
            likeLock.unlock();
        }
    }

    public void view(long postId) {
        viewLock.lock();
        try {
            postRedisRepository.findById(postId).ifPresentOrElse(
                    post -> {
                        post.setViews(post.getViews() + 1);
                        postRedisRepository.save(post);
                        log.info("View added to post with id = {} in cache", postId);
                    },
                    () -> log.warn("View didn't add to post. Cause: post not found by id {} in cache", postId));
        } finally {
            viewLock.unlock();
        }
    }

    public void addComment(long postId, CommentDto comment) {
        postRedisRepository.findById(postId).ifPresentOrElse(
                post -> putComment(postId, comment),
                () -> log.warn("Comment id = {} didn't add to post. Cause: post not found by id {} in cache",
                        comment.id(), postId));
    }

    public void saveComments(long postId, List<CommentDto> comments) {
        comments.forEach(comment -> putComment(postId, comment));
        log.info("Saved to cache {} comments for postId = {}", comments.size(), postId);
    }

    public List<CommentDto> getComments(long postId) {
        String key = KEY_SET_PATTERN.formatted(feedProps.post().key(), postId, feedProps.post().keySet());
        ZSetOperations<String, String> redisSet = stringRedisTemplate.opsForZSet();
        Set<String> comments = redisSet.reverseRange(key, 0, -1);

        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("Got {} comments from cache by postId = {}", comments.size(), postId);
        return comments.stream()
                .map(comment -> jsonMapper.fromJson(comment, CommentDto.class))
                .toList();
    }

    public Optional<PostRedis> findById(long postId) {
        return postRedisRepository.findById(postId);
    }

    public void save(PostDto post) {
        PostRedis postRedis = postMapper.toPostRedis(post);
        postRedis.setTtl(feedProps.post().ttl());
        postRedisRepository.save(postRedis);
        log.info("Saved to cache postId = {}", postRedis.getId());
    }

    public void save(Post post) {
        PostRedis postRedis = postMapper.toPostRedis(post);
        postRedis.setTtl(feedProps.post().ttl());
        postRedisRepository.save(postRedis);
        log.info("Saved to cache postId = {}", postRedis.getId());
    }

    private void putComment(long postId, CommentDto comment) {
        String key = KEY_SET_PATTERN.formatted(feedProps.post().key(), postId, feedProps.post().keySet());
        String commentJson = jsonMapper.toJson(comment);
        stringRedisTemplate.opsForZSet().add(key, commentJson, comment.createdAt().toEpochSecond(ZoneOffset.UTC));
        trimFeedIfNecessary(key);
        redisTemplate.expire(key, Duration.ofSeconds(feedProps.comment().ttl()));
        log.info("Added commentId = {} to postId = {}", comment.id(), postId);
    }

    private void trimFeedIfNecessary(String key) {
        Long feedSize = redisTemplate.opsForZSet().size(key);
        if (feedSize != null && feedSize > feedProps.comment().limit()) {
            redisTemplate.opsForZSet().removeRange(key, 0, feedSize - feedProps.comment().limit() - 1);
            log.info("Removed comments from feed {}. Cause: max storage limit is exceeded", key);
        }
    }
}
