package faang.school.postservice.consumer;

import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractConsumer<T> {
    protected final FeedService feedService;

    protected void handle(T event, Acknowledgment ack, Runnable runnable){
        log.info("New event received: {}", event);
        runnable.run();
        ack.acknowledge();
    }
}
