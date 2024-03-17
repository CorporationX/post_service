package faang.school.postservice.validation;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    public void validateCommentFields(CommentDto commentDto) {
        if (commentDto.getContent() == null || commentDto.getContent().isBlank()) {
            throw new DataValidationException("Comment must have a content");
        }
        if (commentDto.getContent().length() > 4096) {
            throw new DataValidationException("Comment length must be less than 4096 symbols");
        }
    }
}
