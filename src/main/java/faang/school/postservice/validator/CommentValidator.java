package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userServiceClient;

    public void validateUserBeforeCreate(CommentDto commentDto) {
        validateUserExists(commentDto);
        validatePostIdNotNull(commentDto.getPostId());
    }

    private void validatePostIdNotNull(Long postId) {
        if(postId == null) {
            throw new DataValidationException("Post's comment id cannot be null");
        }
    }

    private void validateUserExists(CommentDto commentDto) {
        if(commentDto.getAuthorId() == null ) {
            throw new DataValidationException("Author's comment id cannot be null");
        }
        UserDto user = userServiceClient.getUser(commentDto.getAuthorId());
        if (user == null) {
            throw new EntityNotFoundException("User not found with the given authorId: " + commentDto.getAuthorId());
        }
    }

    public void validateBeforeUpdate(Comment comment, @Valid CommentDto dto) {
        if (!Objects.equals(comment.getAuthorId(), dto.getAuthorId())) {
            throw new DataValidationException("Comment author cannot be changed");
        }

        Long postId = Optional.ofNullable(comment.getPost())
                .map(Post::getId)
                .orElse(null);

        if (!Objects.equals(postId, dto.getPostId())) {
            throw new DataValidationException("Comment post cannot be changed");
        }
    }
}
