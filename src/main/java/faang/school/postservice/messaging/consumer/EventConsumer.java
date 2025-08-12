package faang.school.postservice.messaging.consumer;

/**
 * Универсальный интерфейс для обработки событий из внешних источников (например, Kafka).
 *
 * @param <E> тип события, которое обрабатывает реализация
 * @author Myrza
 * @since 05.08.2025
 */
public interface EventConsumer<E> {
    void consume(E event);
}
