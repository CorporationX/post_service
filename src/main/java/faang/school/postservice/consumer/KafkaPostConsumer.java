package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.PostCreatedEvent;
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
        Long postId = event.getPostId();
        Long authorId = event.getAuthorId();
        log.info("Received PostCreatedEvent: {}", postId);

        PostRedisDto post = PostRedisDto.builder()
                .id(postId)
                .authorId(authorId)
                .text(event.getText())
                .projectId(event.getProjectId())
                .likeCount(0L)
                .createdAt(Instant.ofEpochMilli(event.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        String postKey = "post:" + postId;
        redisTemplate.opsForValue().set(postKey, post, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));

        String userKey = "user:" + authorId;
        if (!redisTemplate.hasKey(userKey)) {
            UserDto author = userServiceClient.getUser(authorId);
            redisTemplate.opsForValue().set(userKey, author, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));
        }

        feedManagementService.addPostToFeeds(postId, event.getTimestamp(), event.getSubscriberIds());
        log.info("Added post {} to feeds of subscribers successfully.", postId);
    }

    @Override
    protected void logError(Exception e) {
        log.error("Error processing PostCreatedEvent", e);
    }
}
