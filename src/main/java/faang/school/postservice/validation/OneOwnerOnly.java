package faang.school.postservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OneOwnerOnlyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OneOwnerOnly {
    String message() default "Post must have either authorId or projectId, not both or neither";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}