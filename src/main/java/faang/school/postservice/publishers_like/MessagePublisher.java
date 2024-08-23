package faang.school.postservice.publishers_like;

public interface MessagePublisher<T> {
    public void publish(T event);
}