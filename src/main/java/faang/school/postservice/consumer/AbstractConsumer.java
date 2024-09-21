package faang.school.postservice.consumer;

import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractConsumer<T> {
    protected final FeedService feedService;

    public abstract void listen(T event);
}
