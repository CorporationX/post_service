package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.CommentDtoStatus;
import org.springframework.stereotype.Component;

@Component
public class CommentIdValidatorFilter implements CommentValidationFilter{
    @Override
    public boolean isApplicable(CommentDtoStatus status) {
        return status.equals(CommentDtoStatus.UPDATE);
    }

    @Override
    public CommentDto apply(CommentDto dto) {
        if(null == dto.getId()){
            throw new DataValidationException
                    ("ID field should be filled in for this operation");
        }
        return dto;
    }
}
