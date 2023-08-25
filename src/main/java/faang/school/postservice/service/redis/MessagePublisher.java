package faang.school.postservice.service.redis;

public interface MessagePublisher {

    void publish(Object message);
}
