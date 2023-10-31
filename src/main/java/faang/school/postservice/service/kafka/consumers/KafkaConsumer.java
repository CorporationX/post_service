package faang.school.postservice.service.kafka.consumers;

public interface KafkaConsumer {

    void listen(String message);
}
