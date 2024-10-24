package faang.school.postservice.annotation.like;

import faang.school.postservice.model.NotificationEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotificationEvent {
    NotificationEventType value();
}