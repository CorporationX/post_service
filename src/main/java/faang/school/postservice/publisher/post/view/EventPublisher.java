package faang.school.postservice.publisher.post.view;

public interface EventPublisher<T> {
    void publish(T event);
    Class<T> getEventType();
}
