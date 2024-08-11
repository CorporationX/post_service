package faang.school.postservice.config.context.redis;

import org.springframework.data.redis.connection.Message;

public interface MessagePublisher {
    void publish(String message);
}
