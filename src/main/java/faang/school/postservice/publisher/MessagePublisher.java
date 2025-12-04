package faang.school.postservice.publisher;

public interface MessagePublisher<T> {
    void publishMessage(T event);
}
