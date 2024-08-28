package faang.school.postservice.publishers.pub;

public interface MessagePublisher<T> {
    void publish(T source);
}