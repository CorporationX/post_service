package faang.school.postservice.validator.annotation;

import faang.school.postservice.annotation.ValidHashtag;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HashtagAnnotationValidator implements ConstraintValidator<ValidHashtag, String> {

    private static final String HASHTAG_PATTERN = "^#[A-Za-z0-9А-Яа-я_]+$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.matches(HASHTAG_PATTERN);
    }
}
