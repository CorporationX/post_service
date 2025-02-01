package faang.school.postservice.kafka.producer;

public interface KafkaSender {

    void sendMessage(String topicName, Object message);
}
