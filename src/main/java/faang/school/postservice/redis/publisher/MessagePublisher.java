package faang.school.postservice.redis.publisher;

public interface MessagePublisher {
    void publish(String message);
}
