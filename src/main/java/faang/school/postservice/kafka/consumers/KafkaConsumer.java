package faang.school.postservice.kafka.consumers;

public interface KafkaConsumer {

    void listen(String message);
}
