package faang.school.postservice.message.producer;

public interface MessagePublisher {
    void publish(String channel, Object message);
}
