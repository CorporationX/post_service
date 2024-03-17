package faang.school.postservice.validation;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.postservice.utils.GlobalValidator.validateOptional;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public void validateCommentFields(CommentDto commentDto) {
        if (commentDto.getContent() == null || commentDto.getContent().isBlank()) {
            throw new DataValidationException("Comment must have a content");
        }
        if (commentDto.getContent().length() > 4096) {
            throw new DataValidationException("Comment length must be less than 4096 symbols");
        }
    }

    public void validateCommentUpdate(Long commentId, CommentDto commentDto) {
        CommentDto existingComment = commentMapper.toDto(validateOptional(commentRepository.findById(commentId), "Comment does not exist"));
        if (!existingComment.getAuthorId().equals(commentDto.getAuthorId())) {
            throw new DataValidationException(commentDto.getAuthorId() + " is not the author of this comment");
        }
        if (!existingComment.getLikeIds().equals(commentDto.getLikeIds())) {
            throw new DataValidationException("Comment likes cannot be updated");
        }
        if (!existingComment.getCreatedAt().equals(commentDto.getCreatedAt())) {
            throw new DataValidationException("Comment created timestamp cannot be updated");
        }
        if (!existingComment.getUpdatedAt().equals(commentDto.getUpdatedAt())) {
            throw new DataValidationException("Comment updated timestamp cannot be updated");
        }
    }
}
