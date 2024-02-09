package faang.school.postservice.validation.comment;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.comment.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommentValidation {

    public void validateCommentData(CommentDto comment) {

    }

    public void validateCommentAuthor(CommentDto comment) {

    }

}
