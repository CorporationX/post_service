package faang.school.postservice.messaging.producer;

import faang.school.postservice.model.message.Event;

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
    boolean isApplicable(Event type);
    void send(E event);
}
