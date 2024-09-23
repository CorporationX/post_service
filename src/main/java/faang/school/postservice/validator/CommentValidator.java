package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userClient;
    private final CommentRepository commentRepository;

    private Comment getComment(CommentDto commentDto) {
        return commentRepository.findById(commentDto.getId()).orElseThrow();
    }

    public void existsAuthor(UserDto user) {
        if (user == null) {
            throw new ValidationException("Author name is required");
        }
    }

    public void validateAuthorIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getAuthorId() == getComment(commentDto).getAuthorId())) {
            throw new ValidationException("Author name can't be changed");
        }

    }

    public void validatePostIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getPostId() == getComment(commentDto).getPost().getId())) {
            throw new ValidationException("Post id can't be changed");
        }
    }

    public void validateAuthorNameUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getAuthorName().equals(userClient.getUser(commentDto.getAuthorId()).getUsername()))) {
            throw new ValidationException("Author name can't be changed");
        }
    }

    public void validateCommentIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getId().equals(getComment(commentDto).getId()))) {
            throw new ValidationException("Comment id can't be changed");
        }
    }

    public void validateAuthorDeleteComment(CommentDto commentDto) {
        if (!(commentDto.getAuthorId().equals(commentRepository.findById(commentDto.getId()).get().getAuthorId()))) {
            throw new ValidationException("Comment can't be deleted by this user");
        }
    }
}

