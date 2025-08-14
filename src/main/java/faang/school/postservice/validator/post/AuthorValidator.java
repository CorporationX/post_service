package faang.school.postservice.validator.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.validator.Validator;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AuthorValidator implements Validator<PostCreateDto> {

    public static final String AUTHOR_VALIDATION_ERROR = "The author can be either a user or a project.";

    @Override
    public void validate(PostCreateDto postDto) {
        if ((postDto.authorId() == null && postDto.projectId() == null)
            || (postDto.authorId() != null && postDto.projectId() != null)
        ) {
            throw new ValidationException(AUTHOR_VALIDATION_ERROR);
        }

    }
}
