package faang.school.postservice.messaging;

public interface EventPublisher<T> {

    void publish(T event);

}
