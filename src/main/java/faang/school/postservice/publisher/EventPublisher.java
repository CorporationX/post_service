package faang.school.postservice.publisher;

public interface EventPublisher<T> {
    void publish(T event);
}
