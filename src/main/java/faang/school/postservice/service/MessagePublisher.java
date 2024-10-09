package faang.school.postservice.service;

public interface MessagePublisher<T> {
    void publish(T message);
}