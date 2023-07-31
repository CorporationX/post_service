package faang.school.postservice.validator;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {

    private static final int POST_LENGTH_MAX = 4096;

    public void validationOfPostCreation(PostDto post) {

        if (post.getContent().length() > POST_LENGTH_MAX) {
            throw new DataValidationException("Content is too long");
        }

        if (post.getContent().isBlank() || post.getContent().isEmpty()) {
            throw new DataValidationException("Content is empty");
        }
    }
}
