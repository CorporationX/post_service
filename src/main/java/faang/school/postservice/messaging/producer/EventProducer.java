package faang.school.postservice.messaging.producer;

/**
 * Унифицированный интерфейс для отправки событий в брокер сообщений.
 *
 * @param <E> тип события, которое будет отправлено
 * @author Myrza
 * @since 05.08.2025
 */
public interface EventProducer<E> {
    void send(E event);
}
