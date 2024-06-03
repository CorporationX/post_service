package faang.school.postservice.publisher.kafka;

public interface MessagePublisher<T> {

    void publish(T event);
}
