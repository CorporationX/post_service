package faang.school.postservice.publisher;

import faang.school.postservice.event.redis.RedisEvent;

public interface RedisPublisher<T extends RedisEvent> {
    void publish(T event);
}
