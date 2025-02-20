package faang.school.postservice.validator;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    public void validateUserExists(UserDto user) {
        if (user == null || user.id() == null) {
            throw new DataValidationException("User not found or has invalid ID.");
        }
    }

    public void validatePostExists(Post post) {
        if (post == null) {
            throw new DataValidationException("Post not found.");
        }
    }

    public void validateCommentExists(Comment comment) {
        if (comment == null) {
            throw new DataValidationException("Comment not found.");
        }
    }
}