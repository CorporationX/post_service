package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.UpdateCommentException;
import faang.school.postservice.model.Comment;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentValidator {

    private final UserServiceClient userServiceClient;

    public void validateUserExistence(long userId) {
        try {
            userServiceClient.getUser(userId);
            log.info("User existence validation confirmed with user ID: {}", userId);
        } catch (FeignException e) {
            throw new DataNotFoundException(String.format("User with id=%d doesn't exist", userId));
        }
    }

    public void validateAuthorUpdate(Comment comment, CommentDto updatedComment) {
        Long authorId = comment.getAuthorId();
        Long projectId = comment.getPost().getId();
        Long updateAuthorId = updatedComment.getAuthorId();
        Long updateProjectId = updatedComment.getPostId();

        if (updateAuthorId == null || !updateAuthorId.equals(authorId)) {
            throw new UpdateCommentException("Author of the comment cannot be deleted or changed");
        } else if (updateProjectId == null || !updateProjectId.equals(projectId)) {
            throw new UpdateCommentException("Author of the comment cannot be deleted or changed");
        }
    }
}
