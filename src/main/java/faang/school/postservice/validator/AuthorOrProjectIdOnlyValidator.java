package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AuthorOrProjectIdOnlyValidator implements ConstraintValidator<AuthorOrProjectIdOnly, PostDto> {
    @Override
    public boolean isValid(PostDto postDto, ConstraintValidatorContext context) {
        if (postDto == null) {
            return true;
        }

        boolean hasAuthorId = postDto.getAuthorId() != null;
        boolean hasProjectId = postDto.getProjectId() != null;

        return (hasAuthorId || hasProjectId) && !(hasAuthorId && hasProjectId);
    }
}
