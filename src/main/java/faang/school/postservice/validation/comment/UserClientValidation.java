package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.exception.comment.CommentAuthorNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientValidation {
    private final UserServiceClient userServiceClient;

    public void checkUser(long userId) {
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            log.error(ExceptionMessages.COMMENT_NOT_FOUND + " " + userId);
            throw new CommentAuthorNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND);
        }
    }
}
