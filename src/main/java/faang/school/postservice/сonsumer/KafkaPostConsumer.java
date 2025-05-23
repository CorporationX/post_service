package faang.school.postservice.сonsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostCreatedEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.properties.FeedCacheProperties;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedCacheProperties feedCacheProperties;
    private final FeedService feedService;
    private final UserServiceClient userServiceClient;

    @KafkaListener(topics = "posts", groupId = "post-service-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            PostCreatedEvent event = objectMapper.readValue(record.value(), PostCreatedEvent.class);

            log.info("Получено событие о новом посте: {}", event.getPostId());

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

            if (!Boolean.TRUE.equals(redisTemplate.hasKey(userKey))) {
                UserDto author = userServiceClient.getUser(event.getAuthorId());
                redisTemplate.opsForValue().set(userKey, author, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));
            }

            feedService.addPostToFeeds(event.getPostId(), event.getTimestamp(), event.getSubscriberIds());
        } catch (Exception e) {
            log.error("Ошибка обработки KafkaPostConsumer", e);
        }
    }

}
