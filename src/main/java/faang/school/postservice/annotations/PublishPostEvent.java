package faang.school.postservice.annotations;

import faang.school.postservice.model.event.post.PostEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishPostEvent {
    PostEventType[] eventTypes();
}
