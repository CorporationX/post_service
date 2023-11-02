package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.TimedPostId;
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
    private final RedisPostMapper redisPostMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisCommentMapper redisCommentMapper;
    private final UserServiceClient userServiceClient;
    private final KafkaFeedProducer kafkaFeedProducer;
    private final RedisKeyValueTemplate redisTemplate;
    private final CommentRepository commentRepository;

    @Value("${comment.cache.max-comments}")
    private Integer maxCommentsInCache;
    @Value("${post.feed.feed-size}")
    private Integer postsFeedSize;

    public void putPostAndAuthorInCache(PostDto post) {
        RedisUser redisUser = getOrSaveUserInCache(post.getAuthorId());

        RedisPost redisPost = redisPostMapper.toRedisPost(post);

        redisPost.setRedisCommentDtos(getCachedComments(post.getComments()));

        redisPostRepository.save(redisPost);
        TimedPostId timedPostId = TimedPostId.builder()
                .postId(post.getId())
                .publishedAt(post.getPublishedAt())
                .build();
        kafkaFeedProducer.sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timedPostId);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addPostInFeed(KafkaPostDto kafkaPostDto) {
        Optional<RedisFeed> optional = redisFeedRepository.findById(kafkaPostDto.getUserId());
        if (optional.isPresent()) {
            RedisFeed redisFeed = optional.get();
            SortedSet<TimedPostId> postIds = redisFeed.getPostIds();
            if (postIds.size() >= postsFeedSize) {
                postIds.remove(postIds.first());
            }
            postIds.add(kafkaPostDto.getPost());
            redisFeed.setPostIds(postIds);
            redisTemplate.update(redisFeed);
        } else {
            SortedSet<TimedPostId> postIds = new TreeSet<>();
            postIds.add(kafkaPostDto.getPost());
            RedisFeed newFeed = RedisFeed.builder()
                    .userId(kafkaPostDto.getUserId())
                    .postIds(postIds)
                    .build();
            redisFeedRepository.save(newFeed);
        }
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void deletePostFromFeed(KafkaPostDto kafkaPostDto) {
        redisFeedRepository.findById(kafkaPostDto.getUserId()).ifPresent(redisFeed -> {
            SortedSet<TimedPostId> postIds = redisFeed.getPostIds();
            postIds.remove(kafkaPostDto.getPost());
            redisFeed.setPostIds(postIds);
            redisTemplate.update(redisFeed);
        });
    }

    public void deletePostFromCache(PostDto post) {
        RedisUser redisUser = getOrSaveUserInCache(post.getAuthorId());

        TimedPostId timedPostId = TimedPostId.builder()
                .postId(post.getId())
                .publishedAt(post.getPublishedAt())
                .build();
        kafkaFeedProducer.sendFeed(KafkaKey.DELETE, redisUser.getFollowerIds(), timedPostId);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void updatePostInCache(PostDto post) {
        redisPostRepository.findById(post.getId()).ifPresent(redisPost -> {
            redisPost.setContent(post.getContent());
            redisPost.setUpdatedAt(post.getUpdatedAt());
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addCommentToPost(CommentDto comment) {
        redisPostRepository.findById(comment.getPostId()).ifPresent(redisPost -> {
            getOrSaveUserInCache(comment.getAuthorId());
            List<RedisCommentDto> comments = redisPost.getRedisCommentDtos();
            if (comments == null) {
                comments = new ArrayList<>();
            }
            if (comments.size() >= maxCommentsInCache) {
                comments.remove(0);
            }
            comments.add(redisCommentMapper.toRedisDto(comment));
            redisPost.setRedisCommentDtos(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void updateCommentOnPost(CommentDto comment) {
        redisPostRepository.findById(comment.getPostId()).ifPresent(redisPost -> {
            List<RedisCommentDto> comments = redisPost.getRedisCommentDtos();
            if (comments != null) {
                for (RedisCommentDto redisCommentDto : comments) {
                    if (redisCommentDto.getId() == comment.getId()) {
                        redisCommentDto.setContent(comment.getContent());
                        redisCommentDto.setUpdatedAt(comment.getUpdatedAt());
                    }
                }
            }
            redisPost.setRedisCommentDtos(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void deleteCommentFromPost(CommentDto commentDto) {
        redisPostRepository.findById(commentDto.getPostId()).ifPresent(redisPost -> {
            var comments = redisPost.getRedisCommentDtos();
            if (comments != null) {
                comments.removeIf(next -> next.getId() == commentDto.getId());
                if (comments.size() < maxCommentsInCache) {
                    comments.clear();
                    List<Comment> lastComment = commentRepository.findLastThreeComments(redisPost.getId());

                    lastComment.forEach(comment -> comments.add(redisCommentMapper.toRedisDto(comment)));
                    Collections.reverse(comments);
                }
            }
            redisPost.setRedisCommentDtos(comments);
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addLikeOnPost(LikeDto likeDto) {
        redisPostRepository.findById(likeDto.getPostId()).ifPresent(redisPost -> {
            redisPost.likeIncrement();
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromPost(long postId) {
        redisPostRepository.findById(postId).ifPresent(redisPost -> {
            redisPost.likeDecrement();
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void addLikeToComment(long postId, long commentId) {
        redisPostRepository.findById(postId).ifPresent(redisPost -> {
            List<RedisCommentDto> comments = redisPost.getRedisCommentDtos();
            if (comments != null) {
                for (var comment : comments) {
                    if (comment.getId() == commentId) {
                        comment.likeIncrement();
                    }
                }
            }
            redisTemplate.update(redisPost);
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void deleteLikeFromComment(long postId, long commentId) {
        redisPostRepository.findById(postId).ifPresent(redisPost -> {
            var comments = redisPost.getRedisCommentDtos();
            if (comments != null) {
                for (var comment : comments) {
                    if (comment.getId() == commentId) {
                        comment.likeDecrement();
                    }
                }
            }
            redisTemplate.update(redisPost);
        });
    }

    private List<RedisCommentDto> getCachedComments(List<CommentDto> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }
        List<RedisCommentDto> redisCommentDtos = new ArrayList<>();
        if (comments.size() >= maxCommentsInCache) {
            redisCommentDtos = comments.stream()
                    .skip(comments.size() - maxCommentsInCache)
                    .map(redisCommentMapper::toRedisDto)
                    .toList();
        } else {
            redisCommentDtos = comments.stream()
                    .map(redisCommentMapper::toRedisDto)
                    .toList();
        }
        return redisCommentDtos;
    }

    private RedisUser getOrSaveUserInCache(long userId) {
        return redisUserRepository.findById(userId).orElseGet(() -> {
            RedisUser entity = redisUserMapper.toEntity(userServiceClient.getUser(userId));
            return redisUserRepository.save(entity);
        });
    }
}
