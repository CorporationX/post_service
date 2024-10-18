package faang.school.postservice.service.publisher;

public interface MessagePublisher<T> {

    void publish(T message);
}
