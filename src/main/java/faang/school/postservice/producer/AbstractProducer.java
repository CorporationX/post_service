package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AbstractProducer<E> implements Producer<E> {

    private final List<Producer<E>> producers;

    @Override
    public void send(E event) {
        producers.forEach(producer -> producer.send(event));
    }

    protected void trySend(Producer<E> producer, E event) {
        try {
            producer.send(event);
        } catch (Exception ignored) {
        }
    }
}
