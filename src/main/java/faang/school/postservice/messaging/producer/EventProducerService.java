package faang.school.postservice.messaging.producer;

import faang.school.postservice.model.message.Event;

/**
 * EventProducerService — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 06.08.2025
 */
public interface EventProducerService<E> {
    void produce(Event type, E event);
}
