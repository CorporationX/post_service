package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exeption.UserInactiveException;
import faang.school.postservice.exeption.ValidationException;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {

    public void validateCommentContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ValidationException("Content must not be blank");
        }
        if (content.length() > 4096) {
            throw new ValidationException("Content must be at most 4096 characters");
        }
    }

    public void validateUser(Long userId, UserServiceClient userServiceClient) {
        try {
            UserDto user = userServiceClient.getUser(userId);
            if (user == null || Boolean.FALSE.equals(user.active())) {
                throw new UserInactiveException("User is inactive or does not exist");
            }
        } catch (Exception e) {
            throw new UserInactiveException("Error validating user: " + e.getMessage());
        }
    }

    public void validateCommentOwnership(Comment comment, Long postId) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new ValidationException("Comment does not belong to this post");
        }
    }
}
