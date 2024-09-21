package faang.school.postservice.producer;

public interface KafkaProducer<T> {
    void send(T event);
}
