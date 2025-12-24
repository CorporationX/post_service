package faang.school.postservice.dto.post;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishPostEvent {
    boolean async() default  true;

    Class<?> eventClass();
}
