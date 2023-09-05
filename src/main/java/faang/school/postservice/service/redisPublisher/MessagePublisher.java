package faang.school.postservice.service.redisPublisher;

public interface MessagePublisher<T> {
    void publish(T message);
}