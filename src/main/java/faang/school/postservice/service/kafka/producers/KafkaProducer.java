package faang.school.postservice.service.kafka.producers;

public interface KafkaProducer {

    void sendMessage(String message);
}
