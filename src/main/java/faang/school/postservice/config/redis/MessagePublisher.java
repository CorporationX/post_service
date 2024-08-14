package faang.school.postservice.config.redis;

public interface MessagePublisher {
    void publish(String message);
}