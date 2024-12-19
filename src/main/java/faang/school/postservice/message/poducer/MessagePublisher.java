package faang.school.postservice.message.poducer;

public interface MessagePublisher {
    void publish(String channel, Object message);
}
