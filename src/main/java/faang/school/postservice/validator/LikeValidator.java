package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void validatePostId(LikeDto likeDto) {
        if (likeDto.getPostId() == null) {
            throw new DataValidationException("PostId cannot be empty");
        }
    }

    public void validateCommentId(LikeDto likeDto) {
        if (likeDto.getCommentId() == null) {
            throw new DataValidationException("CommentId cannot be empty");
        }
    }

    public void validateUserId(LikeDto likeDto) {
        if (likeDto.getUserId() == null) {
            throw new DataValidationException("UserId cannot be empty");
        }
    }

    public void validateUserExists(LikeDto likeDto) {
        try {
            userServiceClient.getUser(likeDto.getUserId());
        } catch (EntityNotFoundException exception) {
            throw new EntityNotFoundException("User not found");
        }
    }

    public void validatePostExists(LikeDto likeDto) {
        if (!postRepository.existsById(likeDto.getPostId())) {
            throw new EntityNotFoundException("Post not found");
        }
    }

    public void validateCommentExists(LikeDto likeDto) {
        if (!commentRepository.existsById(likeDto.getCommentId())) {
            throw new EntityNotFoundException("Comment not found");
        }
    }

    public void validateLikeOnPostNotExist(LikeDto likeDto) {
        if (likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("Like for this post already exists");
        }
    }

    public void validateLikeOnCommentNotExist(LikeDto likeDto) {
        if (likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("Like for this comment already exists");
        }
    }
}
