package faang.school.postservice.publisher.redis;

public interface MessagePublisher<T> {

    void publish(T event);
}