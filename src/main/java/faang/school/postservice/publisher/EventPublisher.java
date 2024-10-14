package faang.school.postservice.publisher;

import org.springframework.data.redis.listener.Topic;

public interface EventPublisher<T> {
    void publish(T event, Topic topic);
}
