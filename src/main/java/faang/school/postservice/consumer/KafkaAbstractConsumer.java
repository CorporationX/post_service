package faang.school.postservice.consumer;

import org.springframework.kafka.support.Acknowledgment;

public abstract class KafkaAbstractConsumer<E> {
    public abstract void consume(E event, Acknowledgment acknowledgment);
}
