package faang.school.postservice.service.redis;

public interface MessagePublisher {
    void publish(String topic, String message);
}
