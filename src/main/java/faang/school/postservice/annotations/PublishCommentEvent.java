package faang.school.postservice.annotations;

import faang.school.postservice.model.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublishCommentEvent {
    Class<? extends Event>[] events();
}