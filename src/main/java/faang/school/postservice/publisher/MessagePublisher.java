package faang.school.postservice.publisher;

public interface MessagePublisher {
    void publish(String topic, String message);
}
