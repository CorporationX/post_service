package faang.school.postservice.newsfeed.kafka;

import org.springframework.kafka.support.Acknowledgment;

/**
 * EventConsumerCache — интерфейс для Kafka-consumer'ов,
 * которые обрабатывают события и кэшируют их в Redis.
 *
 * @param <T> тип события
 */
public interface EventConsumerCache<T> {
    void consume(T event, Acknowledgment ack);
}