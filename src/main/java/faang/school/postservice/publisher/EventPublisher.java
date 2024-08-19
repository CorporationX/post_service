package faang.school.postservice.publisher;


public interface EventPublisher<E> {
    void publish(E message);
}
