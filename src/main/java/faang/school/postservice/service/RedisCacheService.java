package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.TimePostId;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.kafka.producer.KafkaFeedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final CommentRepository commentRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserMapper redisUserMapper;
    private final CommentMapper commentMapper;
    private final RedisCommentMapper redisCommentMapper;
    private final UserServiceClient userService;
    private final KafkaFeedProducer kafkaFeedProducer;
    private final RedisKeyValueTemplate redisTemplate;

    @Value("${comment.cache.max-comments}")
    private Integer maxCommentsInCache;
    @Value("${post.feed.feed-size}")
    private Integer feedBatchSize;

    public void savePublishedPost(PostDto postDto) {
        RedisUser redisUser = getOrSaveUserInCache(postDto.getAuthorId());

        RedisPost redisPost = redisPostMapper.toEntity(postDto);
        redisPost.setRedisComments(getCachedComments(postDto.getComments()));
        redisPostRepository.save(redisPost);

        TimePostId timePostId = TimePostId.builder()
                .postId(postDto.getId())
                .publishedAt(postDto.getPublishedAt())
                .build();
        kafkaFeedProducer.sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
        log.info("Published post with id={} was saved in Redis Cache successfully", postDto.getId());
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void saveFeed(KafkaPostDto kafkaDto) {
        Optional<RedisFeed> optional = redisFeedRepository.findById(kafkaDto.getUserId());

        if (optional.isPresent()) {
            RedisFeed feed = optional.get();
            SortedSet<TimePostId> postSet = feed.getPostsId();
            postSet.add(kafkaDto.getTimePostId());
            if (postSet.size() > feedBatchSize) {
                postSet.remove(postSet.first());
            }

            redisTemplate.update(feed);
        } else {
            SortedSet<TimePostId> set = new TreeSet<>();
            set.add(kafkaDto.getTimePostId());
            RedisFeed newFeed = RedisFeed.builder()
                    .userId(kafkaDto.getUserId())
                    .postsId(set)
                    .build();

            redisTemplate.update(newFeed);
            redisFeedRepository.save(newFeed);
        }
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void updatePostInCache(PostDto updatedPostDto) {
        redisPostRepository.findById(updatedPostDto.getId()).ifPresent(redisPost -> {
            redisPost.setContent(updatedPostDto.getContent());
            redisPost.setUpdatedAt(updatedPostDto.getUpdatedAt());
            redisTemplate.update(redisPost);
        });
    }

    public void deletePostFromCache(PostDto deletedPostDto) {
        RedisUser redisUser = getOrSaveUserInCache(deletedPostDto.getAuthorId());

        kafkaFeedProducer.sendFeed(KafkaKey.DELETE, redisUser.getFollowerIds(), TimePostId.builder()
                .postId(deletedPostDto.getId())
                .publishedAt(deletedPostDto.getPublishedAt())
                .build());
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void deletePostFromFeed(KafkaPostDto kafkaDto) {
        redisFeedRepository.findById(kafkaDto.getUserId()).ifPresent((feed) -> {
            var setPosts = feed.getPostsId();
            setPosts.remove(kafkaDto.getTimePostId());
            feed.setPostsId(setPosts);
            redisTemplate.update(feed);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void addLikeToPost(LikeDto likeDto) {
        redisPostRepository.findById(likeDto.getPostId()).ifPresent(redisPost -> {
            redisPost.likeIncrement();
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void addLikeToComment(LikeDto likeDto) {
        redisPostRepository.findById(likeDto.getPostId()).ifPresent(redisPost -> {
            var comments = redisPost.getRedisComments();
            if (comments != null) {
                for (var comment : comments) {
                    if (comment.getId() == likeDto.getCommentId()) {
                        comment.likeIncrement();
                    }
                }
            }
            redisPost.setRedisComments(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void deleteLikeFromPost(LikeDto likeDto) {
        redisPostRepository.findById(likeDto.getPostId()).ifPresent(redisPost -> {
            redisPost.likeDecrement();
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void deleteLikeFromComment(LikeDto likeDto) {
        redisPostRepository.findById(likeDto.getPostId()).ifPresent(redisPost -> {
            var comments = redisPost.getRedisComments();
            if (comments != null) {
                for (var comment : comments) {
                    if (comment.getId() == likeDto.getCommentId()) {
                        comment.likeDecrement();
                    }
                }
            }
            redisPost.setRedisComments(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void addCommentToPost(CommentDto commentDto) {
        RedisCommentDto redisComment = redisCommentMapper.toRedisDto(commentDto);

        getOrSaveUserInCache(commentDto.getAuthorId());
        redisPostRepository.findById(commentDto.getPostId()).ifPresent(redisPost -> {
            var comments = redisPost.getRedisComments();

            if (comments == null) {
                comments = new ArrayList<>();
            }
            if (comments.size() >= maxCommentsInCache) {
                comments.remove(0);
            }
            comments.add(redisComment);
            redisPost.setRedisComments(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void deleteCommentFromPost(CommentDto commentDto) {
        redisPostRepository.findById(commentDto.getPostId()).ifPresent(redisPost -> {
            var comments = redisPost.getRedisComments();
            if (comments != null) {
                comments.removeIf(next -> next.getId() == commentDto.getId());

                if (comments.size() < maxCommentsInCache) {
                    comments.clear();
                    List<Comment> lastComment = commentRepository.findThreeLastComments(redisPost.getId());
                    lastComment.forEach(dto -> comments.add(redisCommentMapper.toRedisDto(dto)));
                    Collections.reverse(comments);
                }
            }

            redisPost.setRedisComments(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public void updateCommentInCache(CommentDto commentDto) {
        redisPostRepository.findById(commentDto.getPostId()).ifPresent(redisPost -> {
            var comments = redisPost.getRedisComments();

            if (comments != null) {
                for (var comment : comments) {
                    if (comment.getId() == commentDto.getId()) {
                        comment.setContent(commentDto.getContent());
                        comment.setUpdatedAt(commentDto.getUpdatedAt());
                    }
                }
            }
            redisPost.setRedisComments(comments);
            redisTemplate.update(redisPost);
        });
    }

    private List<RedisCommentDto> getCachedComments(List<CommentDto> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }

        List<RedisCommentDto> redisComments;
        if (comments.size() >= maxCommentsInCache) {
            redisComments = comments.stream()
                    .skip(comments.size() - maxCommentsInCache)
                    .map(redisCommentMapper::toRedisDto)
                    .toList();
        } else {
            redisComments = comments.stream()
                    .map(redisCommentMapper::toRedisDto)
                    .toList();
        }
        return redisComments;
    }

    private RedisUser getOrSaveUserInCache(long userId) {
        return redisUserRepository.findById(userId).orElseGet(() -> {
            UserDto userDto = userService.getUser(userId);
            RedisUser entity = redisUserMapper.toEntity(userDto);
            return redisUserRepository.save(entity);
        });
    }
}
