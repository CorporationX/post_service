package faang.school.postservice.kafkaConsumer;

import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

@RequiredArgsConstructor
@Slf4j
abstract class AbstractKafkaConsumer<T> {

    protected final FeedService feedService;
    protected void handle(T event, Acknowledgment ack, Runnable runnable){
        log.info("New event received: {}", event);
        runnable.run();
        ack.acknowledge();
    }
}
