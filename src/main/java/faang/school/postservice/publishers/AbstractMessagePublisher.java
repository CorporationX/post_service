package faang.school.postservice.publishers;

import faang.school.postservice.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractMessagePublisher<T, E extends Event> implements MessagePublisher<T> {
    private final ChannelTopic channel;
    private final RedisTemplate<String, Event> redisTemplate;

    @Override
    public void publish(T source) {
        E event = mapper(source);
        redisTemplate.convertAndSend(channel.getTopic(), event);
        log.info("Published event: {}", event);
        log.debug("Channel: {}", channel.getTopic());
    }
    abstract E mapper(T t);
}
