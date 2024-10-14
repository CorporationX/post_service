package faang.school.postservice.publisher;

public interface MessagePublisher <T> {

    public void publish(T message);
}
