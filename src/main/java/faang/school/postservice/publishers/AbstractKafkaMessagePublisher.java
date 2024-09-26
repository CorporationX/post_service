package faang.school.postservice.publishers;

import faang.school.postservice.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaMessagePublisher<T, E extends Event> implements MessagePublisher<T> {
    private final String channel;
    private final KafkaTemplate<String, E> kafkaTemplate;

    @Override
    public void publish(T source) {
        E event = mapper(source);
        kafkaTemplate.send(channel, event);
        log.info("Published event: {}", event);
        log.debug("Channel: {}", channel);
    }

    public abstract E mapper(T t);
}
