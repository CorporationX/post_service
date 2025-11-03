package faang.school.postservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveIdValidator implements ConstraintValidator<PositiveId, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return value != null && value > 0;
    }
}
