package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.post.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public void checkUserIsExist(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataValidationException("User doesn't exist in the system ID = " + userId);
        }
    }

    public void checkCommentIsExist(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new DataValidationException("Comment doesn't exist in the system ID = " + commentId);
        }
    }

    public void checkPostIsExist(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new DataValidationException("Post doesn't exist in the system ID = " + postId);
        }
    }

    public void checkComment(LikeDto likeDto) {
        if (likeDto.getCommentId() != null) {
            if (!commentRepository.existsById(likeDto.getCommentId())) {
                throw new faang.school.postservice.exception.DataValidationException("no such postId exists commentId: " + likeDto.getCommentId());
            }
        } else {
            throw new faang.school.postservice.exception.DataValidationException("arrived likeDto with postId and commentId equal to null");
        }
    }

}
