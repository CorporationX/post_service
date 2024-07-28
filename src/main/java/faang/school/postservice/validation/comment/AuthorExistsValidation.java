package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorExistsValidation implements CommentValidator{
    private final UserServiceClient userServiceClient;

    @Override
    public void validate(CommentDto commentDto) {
        UserDto user = userServiceClient.getUser(commentDto.getAuthorId());
        if (user == null) {
            log.error(ExceptionMessages.COMMENT_NOT_FOUND);
            throw new EntityNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND);
        }
    }
}
