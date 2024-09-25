package faang.school.postservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AuthorOrProjectIdOnlyValidator.class})
@Target(ElementType.TYPE)
public @interface AuthorOrProjectIdOnly {
    String message() default "Either authorId or projectId must be provided, but not both.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
