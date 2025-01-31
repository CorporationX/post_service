package faang.school.postservice.producer;

public interface KafkaPublisher<T> {
    void publish(T event);
}
