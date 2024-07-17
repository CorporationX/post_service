package faang.school.postservice.validator;

import faang.school.postservice.annotation.ValidHashtag;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HashtagValidator implements ConstraintValidator<ValidHashtag, String> {

    private static final String HASHTAG_PATTERN = "^#[A-Za-z0-9_]+$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Можно изменить на false, если хотите проверять на null
        }
        return value.matches(HASHTAG_PATTERN);
    }
}
