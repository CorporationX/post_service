package faang.school.postservice.validation.comment;

import faang.school.postservice.dto.comment.CommentDto;

public interface CommentValidator {
    void validate(CommentDto commentDto);
}
