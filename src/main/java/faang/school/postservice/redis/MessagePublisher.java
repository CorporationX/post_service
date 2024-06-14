package faang.school.postservice.redis;

public interface MessagePublisher {
    void publish(Object message);
}
