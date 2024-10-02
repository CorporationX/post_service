package faang.school.postservice.producer;

public interface Producer<E> {
    void send(E event);
}
