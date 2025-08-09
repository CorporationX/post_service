package faang.school.postservice.messaging.producer;

/**
 * EventProducer — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 05.08.2025
 */
public interface EventProducer<E> {
    void send(E event);
}
