package faang.school.postservice.messaging.consumer;

/**
 * EventConsumer — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 05.08.2025
 */
public interface EventConsumer<E> {
    void consume(E event);
}
