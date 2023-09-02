package faang.school.postservice.annotations;

import faang.school.postservice.validator.LikeDtoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Constraint(validatedBy = LikeDtoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateLikeDto {
    String message() default "Invalid LikeDto";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}