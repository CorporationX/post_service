package faang.school.postservice.service.redisPublisher;

import org.springframework.data.redis.listener.ChannelTopic;

public interface MessagePublisher<T> {
    void publish(T message);
}
