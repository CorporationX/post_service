package faang.school.postservice.util.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.util.exceptionHandler.ErrorCommentMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentServiceValidator {
    private final UserServiceClient userServiceClient;

    public void validateExistingUserAtCommentDto(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null || userDto.getId() == null) {
            throw new NotFoundException(ErrorCommentMessage.getAuthorIdWasNotFoundMessage(commentDto.getAuthorId()));
        }
    }

    public void validateUpdateComment(Comment comment, CommentDto commentDto) {
        if (comment.getAuthorId() != commentDto.getAuthorId() ||
            comment.getPost().getId() != commentDto.getPostId()) {
            throw new DataValidationException(ErrorCommentMessage.getUpdateNotValidMessage());
        }
    }
}
