package faang.school.postservice.messaging.publishing;

public interface Publishable<T> {
    void publish(T event);
}
