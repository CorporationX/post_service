package faang.school.postservice.publishers.redis;

public interface MessagePublisher<T> {
    void publish(T source);
}