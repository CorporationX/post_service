package faang.school.postservice.listener;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractKafkaListener<T> {

    public void consume(String message, Class<T> type, Consumer<T> consumer) {
        T event = getEvent(message, type);
        consumer.accept(event);
    }

    protected T getEvent(String message, Class<T> type) {
        return type.cast(message);
    }

    public abstract void handle(T event);
}
