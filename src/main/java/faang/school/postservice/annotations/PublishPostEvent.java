package faang.school.postservice.annotations;

import faang.school.postservice.model.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishPostEvent {
    Class<? extends Event>[] events();
}
