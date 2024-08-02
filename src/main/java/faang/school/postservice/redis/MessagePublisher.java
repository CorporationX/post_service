package faang.school.postservice.redis;

public interface MessagePublisher {
    void publish(String message);
}