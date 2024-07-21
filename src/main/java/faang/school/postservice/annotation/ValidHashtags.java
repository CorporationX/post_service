package faang.school.postservice.annotation;

import faang.school.postservice.validator.annotation.HashtagsAnnotationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = HashtagsAnnotationValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHashtags {
    String message() default "Invalid hashtag format in list";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}