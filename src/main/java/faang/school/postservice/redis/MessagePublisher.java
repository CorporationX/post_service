package faang.school.postservice.redis;

public interface MessagePublisher {

    void publishMessage(String message);
}
