package faang.school.postservice.consumer;

public interface MessageConsumer<T> {
    void consume(T event);
}
