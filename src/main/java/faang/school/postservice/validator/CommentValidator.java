package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userServiceClient;

    public void validateUserBeforeCreate(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null || userDto.getId() == null) {
            throw new EntityNotFoundException("User not found with the given authorId: " + commentDto.getAuthorId());
        }
    }

    public void validateBeforeUpdate(Comment comment, CommentDto dto) {
        if (comment.getAuthorId() != dto.getAuthorId() || comment.getPost().getId() != dto.getPostId()) {
            throw new DataValidationException("Comment author cannot be changed");
        }
    }
}
