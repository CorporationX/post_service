package faang.school.postservice.service;

import faang.school.postservice.config.props.CacheTtlProperties;
import faang.school.postservice.mapper.NewsFeedMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.CacheAuthor;
import faang.school.postservice.model.cache.CacheComment;
import faang.school.postservice.model.cache.CachePost;
import faang.school.postservice.repository.cache.CacheAuthorRepository;
import faang.school.postservice.repository.cache.CachePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Supplier;

import static faang.school.postservice.model.cache.CacheAuthor.PROJECT_PREFIX;
import static faang.school.postservice.model.cache.CacheAuthor.USER_PREFIX;
import static faang.school.postservice.model.cache.CacheComment.COMMENT_PREFIX;

@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private static final String FEED_PREFIX = "user_feed:";
    private static final int NEWS_FEED_RANGE_START = 0;
    private static final int FEED_END_INDEX = 1;
    private static final String LIKE_PREFIX = "post_likes:";

    private final CachePostRepository cachePostRepository;
    private final NewsFeedMapper newsFeedMapper;
    private final CacheAuthorRepository cacheAuthorRepository;
    private final UserService userService;
    private final CacheTtlProperties cacheTTLProperties;
    private final ProjectService projectService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${news-feed.max-count-visible-comments:3}")
    private int maxComments;

    @Value("${news-feed.max-posts-in-feed:500}")
    private int maxPostsInFeed;


    public void cacheCommentForPost(Comment comment) {
        if (!cachePostRepository.existsById(comment.getPost().getId())) {
            cachePost(comment.getPost());
        }
        CacheComment cacheComment = newsFeedMapper.toCache(comment);
        cacheComment.setAuthorId(cacheUserAsAuthor(comment.getAuthorId()).getId());
        String cacheKey = COMMENT_PREFIX + comment.getPost().getId();

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForList().leftPush(cacheKey, cacheComment);
                operations.opsForList().trim(cacheKey, 0, maxComments - 1);

                return operations.exec();
            }
        });
    }

    public CachePost cachePost(Post post) {
        var optionalPost = cachePostRepository.findById(post.getId());
        if (optionalPost.isPresent()) {
            return optionalPost.get();
        }
        CachePost cachePost = newsFeedMapper.toCache(post, cacheTTLProperties.getPost().toMillis());
        if (post.getAuthorId() != null) {
            cachePost.setAuthorId(
                    cacheUserAsAuthor(post.getAuthorId()).getId()
            );
        } else if (post.getProjectId() != null) {
            cachePost.setAuthorId(
                    cacheProjectAsAuthor(post.getProjectId()).getId()
            );
        }
        return cachePostRepository.save(cachePost);
    }

    public CacheAuthor cacheUserAsAuthor(long userId) {
        return cacheAuthor(
                USER_PREFIX + userId,
                () -> newsFeedMapper.toCache(
                        userService.getUserDtoById(userId),
                        cacheTTLProperties.getAuthor().toMillis()
                ));
    }

    public CacheAuthor cacheProjectAsAuthor(long projectId) {
        return cacheAuthor(
                PROJECT_PREFIX + projectId,
                () -> newsFeedMapper.toCache(
                        projectService.getProjectById(projectId),
                        cacheTTLProperties.getAuthor().toMillis()
                ));
    }

    public void addPostForNewsFeed(Post post, List<Long> followersIds) {
        long postId = post.getId();
        long timestamp = post.getPublishedAt().toEpochSecond(ZoneOffset.UTC);
        followersIds.parallelStream()
                .forEach(id -> {
                    var cacheKey = FEED_PREFIX + id;
                    redisTemplate.opsForZSet().add(cacheKey, postId, timestamp);
                    redisTemplate.opsForZSet().removeRange(
                            cacheKey, NEWS_FEED_RANGE_START, -maxPostsInFeed - FEED_END_INDEX
                    );
                });
    }

    public void addLikeToPost(long postId) {
        redisTemplate.opsForValue()
                .increment(LIKE_PREFIX + postId, 1);
    }

    private CacheAuthor cacheAuthor(String cacheAuthorId, Supplier<CacheAuthor> cacheAuthorSupplier) {
        var optionalAuthor = cacheAuthorRepository.findById(cacheAuthorId);
        return optionalAuthor
                .orElseGet(() -> cacheAuthorRepository.save(cacheAuthorSupplier.get()));
    }
}
