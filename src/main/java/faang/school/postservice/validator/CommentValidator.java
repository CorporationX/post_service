package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.OperationNotAvailableException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final String POST_NOT_FOUND_EXCEPTION_MSG = "Post with id:%d is not found";
    private final String COMMENT_NOT_FOUND_EXCEPTION_MSG = "Comment with id:%d is not found";
    private final String USER_OPERATION_NOT_AVAILABLE_EXCEPTION_MSG =
            "User with id:%d is not the author of the comment with id:%d";

    public void existUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.error(e.getMessage());
        }
    }

    public void existPost(long postId) {
        if (!postRepository.existsById(postId)) {
            log.error(String.format(POST_NOT_FOUND_EXCEPTION_MSG, postId));
            throw new EntityNotFoundException(String.format(POST_NOT_FOUND_EXCEPTION_MSG, postId));
        }
    }

    public void existComment(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            log.error(String.format(COMMENT_NOT_FOUND_EXCEPTION_MSG, commentId));
            throw new EntityNotFoundException(String.format(COMMENT_NOT_FOUND_EXCEPTION_MSG, commentId));
        }
    }

    public void checkUserIsAuthorComment(Comment comment, long userId) {
        if (comment.getAuthorId() != userId) {
            log.error(String.format(USER_OPERATION_NOT_AVAILABLE_EXCEPTION_MSG, userId, comment.getId()));
            throw new OperationNotAvailableException(
                    String.format(USER_OPERATION_NOT_AVAILABLE_EXCEPTION_MSG, userId, comment.getId()));
        }
    }
}
