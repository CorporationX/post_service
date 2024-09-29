package faang.school.postservice.producer;

public interface BaseProducer<E> {
    void send(E entity);
}
