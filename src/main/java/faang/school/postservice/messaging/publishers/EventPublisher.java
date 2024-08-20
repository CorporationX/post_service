package faang.school.postservice.messaging.publishers;

public interface EventPublisher<T> {
    void publish(T event);
}
