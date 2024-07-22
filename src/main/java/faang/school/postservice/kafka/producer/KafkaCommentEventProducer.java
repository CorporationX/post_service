package faang.school.postservice.kafka.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.redis.cache.RedisUserCache;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentEventProducer extends AbstractEventProducer {
    private final UserServiceClient userServiceClient;
    private final RedisUserCache redisUserCache;
    public KafkaCommentEventProducer(
            KafkaTemplate<String, EventDto> kafkaTemplate,
            NewTopic commentKafkaTopic,
            UserServiceClient userServiceClient, RedisUserCache redisUserCache) {
        super(kafkaTemplate, commentKafkaTopic);

        this.userServiceClient = userServiceClient;
        this.redisUserCache = redisUserCache;
    }

    @Async
    @Retryable
    public void handleNewCommentEvent(CommentEventDto commentEventDto) {
        sendEvent(commentEventDto, String.valueOf(commentEventDto.getCommentAuthorId()));

        redisUserCache.save(userServiceClient.getUser(commentEventDto.getCommentAuthorId()));
    }
}
