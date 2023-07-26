package faang.school.postservice.util.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.DataValidationException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Component
public class PostControllerValidator {

    @SneakyThrows
    public void validatePost(PostDto postDto) {
        BindingResult bindingResult = new BeanPropertyBindingResult(postDto, "postDto");

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder message = new StringBuilder();

            fieldErrors.forEach(fieldError -> {
                message.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            });

            throw new CreatePostException(message.toString());
        }
    }

    public void validateId(Long id) {
        if (id <= 0) {
            throw new DataValidationException("Id should be greater than 0");
        }
    }
}
