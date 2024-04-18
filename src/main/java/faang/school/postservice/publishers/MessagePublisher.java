package faang.school.postservice.publishers;


public interface MessagePublisher {
    <T>  void publish(T event);
}
