package faang.school.postservice.messaging.publisher;

public interface EventPublisher<T> {

    void publish(T event);

}
