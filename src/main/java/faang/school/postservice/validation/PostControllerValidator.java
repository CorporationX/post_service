package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class PostControllerValidator {
    public void isIdCorrect(Long id) {
        if (id == null) {
            throw new DataValidationException("Invalid postId!");
        }
    }
}
