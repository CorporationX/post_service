package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    public static final int MAX_LENGTH_CONTENT = 4096;

    private final UserServiceClient userServiceClient;

    public void validateCommentAuthor(Long userId) {
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException ex) {
            throw new EntityNotFoundException("User not found");
        }
    }

    public void validateLengthContentComment(String content) {
        if (content.isEmpty()) {
            throw new DataValidationException("the length of the comment is empty");
        }
        if (content.length() > MAX_LENGTH_CONTENT) {
            throw new DataValidationException(
                    "the length of the comment is more than %d characters".formatted(MAX_LENGTH_CONTENT));
        }
    }
}
