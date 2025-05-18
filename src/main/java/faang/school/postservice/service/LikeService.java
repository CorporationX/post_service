package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.ike.CommentLikeDto;
import faang.school.postservice.dto.ike.PostLikeDto;
import faang.school.postservice.exception.LikeException;
import faang.school.postservice.like.TargetLike;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.comment.FeedCommentRedisService;
import faang.school.postservice.service.feed.post.FeedPostRedisService;
import faang.school.postservice.service.kafka.publisher.KafkaPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    public static final String ALREADY_LIKED = "You have already liked this.";
    public static final String POST_NOT_FOUND = "Post not found with id: %d";
    public static final String COMMENT_NOT_FOUND = "Comment not found with id: %d";
    public static final String USER_NOT_FOUND = "User not found with id: %d";
    public static final String LIKE_NOT_FOUND = "No likes found for a comment or post %d from a user with id: %d";
    public static final String BOTH_LIKE = "You cannot like a post and comment at the same time.";
    public static final String ERROR_VALIDATING_USER = "Error occurred when validating a user with id: %d.";

    private final KafkaPublisher kafkaPublisher;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final FeedPostRedisService feedPostRedisService;
    private final FeedCommentRedisService feedCommentRedisService;

    @Value("${spring.kafka.topics.feed.post-like-topic}")
    private String postLikeTopic;

    @Value("${spring.kafka.topics.feed.post-unlike-topic}")
    private String postUnlikeTopic;

    @Value("${spring.kafka.topics.feed.comment-like-topic}")
    private String commentLikeTopic;

    @Value("${spring.kafka.topics.feed.comment-unlike-topic}")
    private String commentUnlikeTopic;

    public void likePost(long postId, long userId) {
        kafkaPublisher.send(postLikeTopic, new PostLikeDto(postId, userId));
    }

    @Transactional
    public void likePostConsumer(PostLikeDto postLikeDto) {
        Long userId = postLikeDto.getUserId();
        Long postId = postLikeDto.getPostId();

        validateUser(userId);
        Post post = getEntity(() -> postRepository.findById(postId), () -> String.format(POST_NOT_FOUND, postId));
        validateNotLiked(postId, userId, TargetLike.POST);

        Like like = buildLike(userId, post, null);
        validateLikesRepeat(post, null);
        likeRepository.save(like);
        feedPostRedisService.incrementPostLikes(postId);
        log.info("User {} liked post {} !", userId, postId);
    }

    public void unlikePost(long postId, long userId) {
        kafkaPublisher.send(postUnlikeTopic, new PostLikeDto(postId, userId));
    }

    @Transactional
    public void unlikePostConsumer(PostLikeDto postLikeDto) {
        Long userId = postLikeDto.getUserId();
        Long postId = postLikeDto.getPostId();

        validateUser(userId);
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            String error = (String.format(LIKE_NOT_FOUND, postId, userId));
            log.error(error);
            throw new LikeException(error);
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        feedPostRedisService.decrementPostLikes(postId);
        log.info("User {} removed a like from a post {}", userId, postId);
    }

    public void likeComment(long commentId, long userId) {
        kafkaPublisher.send(commentLikeTopic, new CommentLikeDto(commentId, userId));
    }

    @Transactional
    public void likeCommentConsumer(CommentLikeDto commentLikeDto) {
        Long userId = commentLikeDto.getUserId();
        Long commentId = commentLikeDto.getCommentId();

        validateUser(userId);
        Comment comment = getEntity(() -> commentRepository.findById(commentId), () ->
                String.format(COMMENT_NOT_FOUND, commentId));

        validateLikesRepeat(null, comment);
        validateNotLiked(commentId, userId, TargetLike.COMMENT);

        Like like = buildLike(userId, null, comment);
        likeRepository.save(like);
        feedCommentRedisService.incrementCommentLikes(commentId);
        log.info("User {} liked comment {} !", userId, commentId);
    }

    public void unlikeComment(long commentId, long userId) {
        kafkaPublisher.send(commentLikeTopic, new CommentLikeDto(commentId, userId));
    }

    @Transactional
    public void unlikeCommentConsumer(CommentLikeDto commentLikeDto) {
        Long userId = commentLikeDto.getUserId();
        Long commentId = commentLikeDto.getCommentId();

        validateUser(userId);
        if (!likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            String error = (String.format(LIKE_NOT_FOUND, commentId, userId));
            log.error(error);
            throw new LikeException(error);
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        feedCommentRedisService.decrementCommentLikes(commentId);
        log.info("User {} removed a like from a comment {}", userId, commentId);
    }

    public Like buildLike(long userId, Post post, Comment comment) {
        validateLikesRepeat(post, comment);
        return Like.builder()
                .userId(userId)
                .post(post)
                .comment(comment)
                .build();
    }

    private <T> T getEntity(Supplier<Optional<T>> finder, Supplier<String> errorMessage) {
        return finder.get()
                .orElseThrow(() -> {
                    String error = errorMessage.get();
                    log.error(error);
                    return new LikeException(error);
                });
    }

    private void validateNotLiked(long targetId, long userId, TargetLike targetLike) {
        boolean exist = switch (targetLike) {
            case POST -> likeRepository.findByPostIdAndUserId(targetId, userId).isPresent();
            case COMMENT -> likeRepository.findByCommentIdAndUserId(targetId, userId).isPresent();
        };

        if (exist) {
            log.error(ALREADY_LIKED);
            throw new LikeException(ALREADY_LIKED);
        }
    }

    private void validateUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            String error = String.format(USER_NOT_FOUND, userId);
            log.error(error);
            throw new LikeException(error);
        } catch (FeignException e) {
            String error = String.format(ERROR_VALIDATING_USER, userId);
            log.error(error, e);
            throw new LikeException(error);
        }
    }

    private void validateLikesRepeat(Post post, Comment comment) {
        if (post != null && comment != null) {
            throw new LikeException(BOTH_LIKE);
        }
        if (post != null && commentRepository.existsById(post.getId())) {
            throw new LikeException(BOTH_LIKE);
        }
    }
}
