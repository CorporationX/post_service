package faang.school.postservice.publisher;

public interface Publisher<T> {
    Class<T> getInstance();
    public void publish(T event, String topic);
}
