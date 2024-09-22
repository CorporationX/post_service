package faang.school.postservice.consumer;

import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.Acknowledgment;

@RequiredArgsConstructor
public abstract class AbstractConsumer<T> {
    protected final FeedService feedService;

    public abstract void listen(T event, Acknowledgment ack);
}
