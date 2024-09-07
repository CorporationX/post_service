package faang.school.postservice.validator.annotation;

import faang.school.postservice.annotation.ValidHashtags;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class HashtagsAnnotationValidator implements ConstraintValidator<ValidHashtags, List<String>> {

    private static final String HASHTAG_PATTERN = "^#[A-Za-z0-9А-Яа-я_]+$";

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String hashtag : value) {
            if (hashtag == null || !hashtag.matches(HASHTAG_PATTERN)) {
                return false;
            }
        }
        return true;
    }
}