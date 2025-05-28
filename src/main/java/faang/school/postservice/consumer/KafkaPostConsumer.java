package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.PostCreatedEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import faang.school.postservice.service.feed.FeedManagementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@Component
public class KafkaPostConsumer extends AbstractKafkaConsumer<PostCreatedEvent> {

    private final FeedManagementService feedManagementService;
    private final UserServiceClient userServiceClient;

    public KafkaPostConsumer(ObjectMapper objectMapper,
                             RedisTemplate<String, Object> redisTemplate,
                             FeedCacheProperties feedCacheProperties,
                             FeedManagementService feedManagementService,
                             UserServiceClient userServiceClient) {
        super(objectMapper, redisTemplate, feedCacheProperties);
        this.feedManagementService = feedManagementService;
        this.userServiceClient = userServiceClient;
    }

    @KafkaListener(topics = "posts", groupId = "post-service-group")
    public void consume(ConsumerRecord<String, String> record) {
        consumeRecord(record, PostCreatedEvent.class);
    }

    @Override
    protected void handleEvent(PostCreatedEvent event) {
        log.info("Received PostCreatedEvent: {}", event.getPostId());

        PostRedisDto post = PostRedisDto.builder()
                .id(event.getPostId())
                .authorId(event.getAuthorId())
                .text(event.getText())
                .projectId(event.getProjectId())
                .likeCount(0L)
                .createdAt(Instant.ofEpochMilli(event.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        String postKey = "post:" + event.getPostId();
        redisTemplate.opsForValue().set(postKey, post, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));

        String userKey = "user:" + event.getAuthorId();
        if (!redisTemplate.hasKey(userKey)) {
            UserDto author = userServiceClient.getUser(event.getAuthorId());
            redisTemplate.opsForValue().set(userKey, author, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));
        }

        feedManagementService.addPostToFeeds(event.getPostId(), event.getTimestamp(), event.getSubscriberIds());
    }

    @Override
    protected void logError(Exception e) {
        log.error("Error processing PostCreatedEvent", e);
    }
}
