package faang.school.postservice.kafka;

public interface KafkaEventProducer<T> {
    void sendMessage(T messageDto);
}
