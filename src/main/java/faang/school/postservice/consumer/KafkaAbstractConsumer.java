package faang.school.postservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class KafkaAbstractConsumer<T> {
    protected void handle(T event, Acknowledgment ack, Runnable runnable) {
        log.info("Event received: {}", event);
        runnable.run();
        ack.acknowledge();
    }
}
