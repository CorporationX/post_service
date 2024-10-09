package faang.school.postservice.config.redis.publisher;

public interface MessagePublisher<T> {
    void publish(T event);
}
