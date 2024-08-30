package faang.school.postservice.annotation;

import faang.school.postservice.validator.annotation.HashtagAnnotationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = HashtagAnnotationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHashtag {
    String message() default "Invalid hashtag format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}