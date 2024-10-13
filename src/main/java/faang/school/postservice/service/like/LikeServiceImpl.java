package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.UserAlreadyLikedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisherImpl;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final LikeMapper likeMapper;
    private final LikeEventPublisherImpl likeEventPublisher;

    @Override
    public LikeDto likePost(Long postId) {
        Long userId = userContext.getUserId();
        validateUserExists(userId);
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new UserAlreadyLikedException("User with id %d is already liked post with id %d"
                    .formatted(userId, postId));
        }
        Like like = Like.builder()
                .userId(userId)
                .post(getPostById(postId))
                .build();
        likeRepository.save(like);
        publishLikeEvent(postId, userId);
        log.info("User with id {} liked post with id {}", userId, postId);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public LikeDto removeLikeOnPost(Long postId) {
        Long userId = userContext.getUserId();
        validateUserExists(userId);
        Like like = likeRepository.findByPostIdAndUserId(postId, userId).orElseThrow(() ->
                new EntityNotFoundException("Like by user with id %d on post with id %d does not exist"
                        .formatted(userId, postId)));
        likeRepository.delete(like);
        log.info("User with id {} removed like from post with id {}", userId, postId);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public LikeDto likeComment(Long commentId) {
        Long userId = userContext.getUserId();
        validateUserExists(userId);
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new UserAlreadyLikedException("User with id %d is already liked comment with id %d"
                    .formatted(userId, commentId));
        }
        Like like = Like.builder()
                .userId(userId)
                .comment(getCommentById(commentId))
                .build();
        likeRepository.save(like);
        log.info("User with id {} like comment with id {}", userId, commentId);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public LikeDto removeLikeOnComment(Long commentId) {
        Long userId = userContext.getUserId();
        validateUserExists(userId);
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId).orElseThrow(() ->
                new EntityNotFoundException("Like by user with id %d on comment with id %d does not exist".formatted(userId, commentId)));
        likeRepository.delete(like);
        log.info("User with id {} removed like from comment with id {}", userId, commentId);
        return likeMapper.toLikeDto(like);
    }

    private void validateUserExists(Long userId) {
        if (userServiceClient.getUser(userId) == null) {
            throw new EntityNotFoundException("User with id %d is not found".formatted(userId));
        }
    }

    private Post getPostById(long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post with id %d does not exist".formatted(postId)));
    }

    private Comment getCommentById(long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Comment with id %d does not exist".formatted(commentId)));
    }

    private void publishLikeEvent(long postId, long userId) {
        LikeEvent likeEvent = LikeEvent.builder()
                .likeAuthorId(userId)
                .postAuthorId(postRepository.findById(postId).orElseThrow(() ->
                        new EntityNotFoundException("Post with id %d doesn't exist".formatted(postId))).getAuthorId())
                .postId(postId)
                .build();
        likeEventPublisher.publish(likeEvent);
        log.info("Publishing LikeEvent for user with id {} liking post with id {}", userId, postId);
    }
}
