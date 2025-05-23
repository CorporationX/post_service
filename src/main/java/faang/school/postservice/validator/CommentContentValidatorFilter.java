package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.CommentDtoStatus;
import org.springframework.stereotype.Component;

@Component
public class CommentContentValidatorFilter implements CommentValidationFilter {

    public static final int CONTENT_MAX_LENGTH = 4096;

    @Override
    public boolean isApplicable(CommentDtoStatus status) {
        return status.equals(CommentDtoStatus.CREATION) || status.equals(CommentDtoStatus.UPDATE);
    }

    @Override

    public CommentDto apply(CommentDto dto) {
        if(dto.getContent().isEmpty() || dto.getContent().isBlank()){
            throw new DataValidationException
                    ("content field should be filled in");
        }
        if (dto.getContent().length() > CONTENT_MAX_LENGTH){
            throw new DataValidationException
                    ("content should be less then %d symbols".formatted(CONTENT_MAX_LENGTH));
        }
            return dto;
    }
}
