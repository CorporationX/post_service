package faang.school.postservice.kafka.producers;

public interface KafkaProducer {

    void sendMessage(String message);
}