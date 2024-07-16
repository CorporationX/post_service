package faang.school.postservice.publisher;

public interface RedisPublisher<T> {
    void publish(T event);
}
