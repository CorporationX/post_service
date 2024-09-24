package faang.school.postservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OnlyPostCreatorValidator.class)
public @interface OnlyPostCreator {

    String message() default "Either authorId or projectId must be provided, but not both";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}