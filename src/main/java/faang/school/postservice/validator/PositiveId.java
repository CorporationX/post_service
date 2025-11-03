package faang.school.postservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveIdValidator.class)
@NotNull
@Min(1)
public @interface PositiveId {
    String message() default "ID must be positive";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

