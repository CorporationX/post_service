package faang.school.postservice.messaging.publishing;

public interface MessagePublisher<T> {
     void publish(T message);
}
