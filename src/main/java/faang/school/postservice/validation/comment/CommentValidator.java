package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userServiceClient;

    public void validateCommentData(CommentDto comment) {
        if (comment.getAuthorId() == null || comment.getId() == null || comment.getContent() == null ||
                comment.getPostId() == null) {
            throw new DataValidationException("CommentDto data is not correct");
        }
    }

    public void validateCommentAuthor(CommentDto comment) {
        UserDto user = userServiceClient.getUser(comment.getAuthorId());
        if(user == null) {
            throw new DataValidationException("User data is not correct");
        }
        if (user.getId() == null || user.getUsername() == null || user.getEmail() == null) {
            throw new DataValidationException("User data is not correct");
        }
    }

}
