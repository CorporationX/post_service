package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;

    public void validateCommentAuthor(Long userId) {
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException ex) {
            throw new EntityNotFoundException("User not found");
        }
    }
}
