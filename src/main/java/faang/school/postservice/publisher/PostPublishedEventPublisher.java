package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostPublishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * Класс-отправитель ивента {@link PostPublishedEvent}
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Slf4j
@Component
public class PostPublishedEventPublisher extends AbstractEventPublisher<PostPublishedEvent> {

    @Value("${redis.topic.post-published}")
    private String topic;

    public PostPublishedEventPublisher(RetryTemplate retryTemplate,
                                       RedisTemplate<String, Object> redisTemplate) {
        super(retryTemplate, redisTemplate);
    }

    @Override
    protected String getTopic() {
        return topic;
    }
}