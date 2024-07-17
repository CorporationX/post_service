package faang.school.postservice.service.redis.post;

import faang.school.postservice.model.redis.CommentRedisCache;
import faang.school.postservice.model.redis.PostRedisCache;
import faang.school.postservice.property.CacheProperty;
import faang.school.postservice.repository.redis.PostRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostRedisCacheServiceImpl implements PostRedisCacheService {

    @Value("${spring.cache.cache-settings.posts.name}")
    private String cacheName;
    @Value("${spring.cache.max-post-comments-size}")
    private long maxCommentsSize;

    private int ttl;
    private final CacheProperty cacheProperty;
    private final PostRedisRepository postRedisRepository;

    @PostConstruct
    public void init() {
        ttl = cacheProperty.getCacheSettings().get(cacheName).getTtl();
    }

    @Override
    @Retryable(retryFor = {OptimisticEntityLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public PostRedisCache save(PostRedisCache entity) {

        entity.setTtl(ttl);
        entity = updateOrSave(entity);

        log.info("Saved post with id {} to cache: {}", entity.getId(), entity);

        return entity;
    }

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void addCommentToPost(CommentRedisCache comment) {

        PostRedisCache post = findById(comment.getPostId());

        if (post == null) {
            return;
        }

        NavigableSet<CommentRedisCache> comments = addCommentAndGet(comment, post);

        if (comments != null) {
            post.setCommentRedisCaches(comments);
            updateOrSave(post);

            log.info("Added comment with id={} to post cache: {}", comment.getId(), post);
        }
    }

    @Override
    public void deleteById(long postId) {

        postRedisRepository.deleteById(postId);
        log.info("Deleted post with id={} from cache", postId);
    }

    private NavigableSet<CommentRedisCache> addCommentAndGet(CommentRedisCache comment, PostRedisCache post) {

        NavigableSet<CommentRedisCache> comments = post.getCommentRedisCaches();

        if (comments == null) {
            comments = Collections.synchronizedNavigableSet(new TreeSet<>());
        }

        if (comments.size() >= maxCommentsSize) {

            CommentRedisCache last = comments.last();

            if (comment.compareTo(last) > 0) {
                comments.pollLast();
                comments.add(comment);
            } else {
                return null;
            }

        } else {
            comments.add(comment);
        }

        return comments;
    }

    private PostRedisCache updateOrSave(PostRedisCache entity) {

        postRedisRepository.findById(entity.getId()).ifPresent(postRedisRepository::delete);
        return postRedisRepository.save(entity);
    }

    private PostRedisCache findById(Long id) {

        return postRedisRepository.findById(id).orElse(null);
    }
}
