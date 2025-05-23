package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentValidator {

    private List<CommentValidationFilter> validationFilters;

    public CommentDto validate(CommentDto commentDto) {
        for (CommentValidationFilter filter : validationFilters) {
            if(filter.isApplicable(commentDto.getStatus())){
                filter.apply(commentDto);
            }
        }
        return commentDto;
    }
}
