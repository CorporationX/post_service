package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userServiceClient;

    public void validateCommentAuthor(Long commentId) {
        UserDto user = userServiceClient.getUser(commentId);
        if (user.getUsername() == null || user.getEmail() == null) {
            throw new DataValidationException("User data is not correct");
        }
    }

}
