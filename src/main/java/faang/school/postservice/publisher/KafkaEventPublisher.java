package faang.school.postservice.publisher;

public interface KafkaEventPublisher<T> {

    void publish(T event);
}
