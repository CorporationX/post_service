package faang.school.postservice.validation;

import faang.school.postservice.dto.post.PostRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneOwnerOnlyValidator implements ConstraintValidator<OneOwnerOnly, PostRequestDto> {

    @Override
    public boolean isValid(PostRequestDto dto, ConstraintValidatorContext context) {
        boolean hasAuthor = dto.authorId() != null;
        boolean hasProject = dto.projectId() != null;
        return hasAuthor ^ hasProject;
    }
}