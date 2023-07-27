package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exeption.DataValidationException;

public class CommentValidator {
    public void idValidator(long id){
        if (id<1){
            throw new DataValidationException("Id mast be more 1, your isn't");
        }
    }

    public void commentDtoValidator(CommentDto commentDto){
        idValidator(commentDto.getId());
        idValidator(commentDto.getAuthorId());
        idValidator(commentDto.getPostId());

        int lenComment = commentDto.getContent().length();
        if(lenComment>=4096 || lenComment==0){
            throw new DataValidationException("Length of comment is not correct");
        }
    }
}
