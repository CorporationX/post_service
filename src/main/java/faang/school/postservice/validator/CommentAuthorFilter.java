package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.CommentDtoStatus;

public class CommentAuthorFilter implements CommentValidationFilter{
    @Override
    public boolean isApplicable(CommentDtoStatus status) {
        return status.equals(CommentDtoStatus.CREATION);
    }

    @Override
    public CommentDto apply(CommentDto dto) {
        if(null == dto.getAuthorId()){
            throw new DataValidationException
                    ("AuthorId field should be filled in for this operation");
        }
        return dto;
    }
}
