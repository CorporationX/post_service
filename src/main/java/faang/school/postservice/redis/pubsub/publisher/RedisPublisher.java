package faang.school.postservice.redis.pubsub.publisher;

import faang.school.postservice.redis.pubsub.event.RedisEvent;

public interface RedisPublisher<T extends RedisEvent> {
    void publish(T event);
}
