package faang.school.postservice.service.like;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeCountDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.LikeReceivedEventDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.DuplicateLikesException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.like.RedisLikeReceivedEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final RedisLikeReceivedEventPublisher likeReceivedEventPublisher;

    @Override
    public LikeDto putLikeToPost(long postId) {
        Post post = getPost(postId);
        checkUserExists();        
        duplicatePostLikeValidation(postId);        
        relatedCommentsLikeValidation(postId);

        Like like = Like.builder()
            .userId(userContext.getUserId())
            .post(post)
            .build();

        LikeReceivedEventDto likeReceivedEventDto = LikeReceivedEventDto.builder()
            .actorId(userContext.getUserId())
            .receiverId(post.getId())
            .eventType("POST_LIKE")
            .receivedAt(LocalDateTime.now().toString())
            .build();
        likeReceivedEventPublisher.publish(likeReceivedEventDto);

        return likeMapper.toDto(likeRepository.save(like));
    }

    @Override
    public LikeDto putLikeToComment(long commentId) {
        Comment comment = getComment(commentId);
        checkUserExists();
        duplicateCommentLikeValidation(commentId);
        relatedPostsLikeValidation(comment);

        Like like = Like.builder()
            .userId(userContext.getUserId())
            .comment(comment)
            .build();

        return likeMapper.toDto(likeRepository.save(like));
    }

    @Override
    public LikeCountDto countLikesForPost(Long postId) {
        return new LikeCountDto(getPost(postId).getLikes().size());
    }
    
    @Override
    public void deleteLikeForPost(long postId) {
        if (likeRepository.findByPostIdAndUserId(postId, userContext.getUserId()).isPresent()) {
            likeRepository.deleteByPostIdAndUserId(postId, userContext.getUserId());
        } else {
            throw new DataValidationException(String.format(
                "User %d has not liked post %d.", userContext.getUserId(), postId
            ));
        }
    }

    @Override
    public void deleteLikeForComment(long commentId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userContext.getUserId()).isPresent()) {
            likeRepository.deleteByCommentIdAndUserId(commentId, userContext.getUserId());
        } else {
            throw new DataValidationException(String.format(
                "User %d has not liked comment %d.", userContext.getUserId(), commentId
            ));
        }
    }

    @Override
    public List<LikeDto> getLikesByUser() {
        List<Like> likes = likeRepository.findByUserId(userContext.getUserId());
        return likeMapper.toDtos(likes);
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new EntityNotFoundException(String.format(
                "Post %d is not found.", postId
            )));
    }

    private void checkUserExists() {
        userServiceClient.getUser(userContext.getUserId());
    }

    private void duplicatePostLikeValidation(long postId) {
        if (likeRepository.findByPostIdAndUserId(postId, userContext.getUserId()).isPresent()) {
            throw new DuplicateLikesException(String.format(
                "User %d already liked post %d.", userContext.getUserId(), postId
            ));
        }
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new EntityNotFoundException(String.format(
                "Comment %d is not found.", commentId
            )));
    }

    private void duplicateCommentLikeValidation(long commentId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userContext.getUserId()).isPresent()) {
            throw new DuplicateLikesException(String.format(
                "User %d already liked comment %d.", 
                userContext.getUserId(), commentId
            ));
        }
    }

    private List<Like> getUsersLikes() {
        return likeRepository.findByUserId(userContext.getUserId());
    }

    private void relatedCommentsLikeValidation(Long postId) {
        List<Long> postsIds = getUsersLikes().stream()
            .filter(like -> like.getComment() != null)
            .map(like -> like.getComment().getPost().getId())
            .toList();
        
        if (postsIds.contains(postId)) {
            throw new DataValidationException(String.format(
                "User %d already liked at least one comment of post %d.", 
                userContext.getUserId(), postId
            ));
        }
    }

    private void relatedPostsLikeValidation(Comment comment) {
        List<Long> postsIds = getUsersLikes().stream()
            .filter(like -> like.getPost() != null)
            .map(like -> like.getPost().getId())
            .toList();
        
        if (postsIds.contains(comment.getPost().getId())) {
            throw new DataValidationException(String.format(
                "User %d already liked the post of comment %d.", 
                userContext.getUserId(), comment.getId()
            ));
        }
    }
}
