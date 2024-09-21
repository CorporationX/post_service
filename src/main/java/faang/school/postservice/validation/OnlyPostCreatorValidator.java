package faang.school.postservice.validation;

import faang.school.postservice.dto.post.request.PostCreationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OnlyPostCreatorValidator implements ConstraintValidator<OnlyPostCreator, PostCreationRequest> {

    @Override
    public boolean isValid(PostCreationRequest request, ConstraintValidatorContext context) {
        return (request.authorId() == null && request.projectId() != null) ||
                (request.authorId() != null && request.projectId() == null);
    }
}
