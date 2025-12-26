package faang.school.postservice.publisher.redis;

public interface MessagePublisher<T> {
    void publishMessage(T event);
}
