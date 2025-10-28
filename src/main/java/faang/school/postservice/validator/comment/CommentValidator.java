package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exeption.UserInactiveException;
import faang.school.postservice.exeption.ValidationException;

import java.util.Objects;

public class CommentValidator {

    public static void validateCommentContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ValidationException("Content must not be blank");
        }
        if (content.length() > 4096) {
            throw new ValidationException("Content must be at most 4096 characters");
        }
    }

    public static void validateUser(UserDto user) {
        try {
            if (user == null || Boolean.FALSE.equals(user.active())) {
                throw new UserInactiveException("User is inactive or does not exist");
            }
        } catch (Exception e) {
            throw new UserInactiveException("Error validating user: " + e.getMessage());
        }
    }

    public static void validateCommentOwnership(Long existingAuthorId, Long userId) {
        if (!Objects.equals(existingAuthorId, userId)) {
            throw new ValidationException("You can't delete someone else's comment.");
        }
    }
}
