package faang.school.postservice.service.messaging;

public interface MessagePublisher<T> {
    void publish(T message);
}