package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;

    public void checkCommentAuthor(CommentDto commentDto) {
        UserDto author = userServiceClient.getUser(commentDto.getAuthorId());
        if (author == null) {
            throw new DataValidationException("Comment author does not exist.");
        }
    }
}
