package faang.school.postservice.producer;

public interface KafkaProducer<T> {
    void produce(T event);
}
