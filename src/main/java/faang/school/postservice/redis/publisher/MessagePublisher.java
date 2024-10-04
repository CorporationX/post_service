package faang.school.postservice.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessagePublisher<T> {
    void publish(T message) throws JsonProcessingException;
}