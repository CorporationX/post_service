package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.events.KafkaPostEvent;
import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.dto.redis.UserRedis;
import faang.school.postservice.mapper.redis.user.UserDtoToUserRedisMapper;
import faang.school.postservice.repository.feed.FeedRedisRepository;
import faang.school.postservice.repository.post.PostRedisRepository;
import faang.school.postservice.repository.user.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private static final String TOPIC = "${spring.kafka1.topics.publish-post.name}";
    private static final String GROUP_ID = "${spring.kafka1.consumer.group-id}";

    private final ObjectMapper objectMapper;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;
    private final UserDtoToUserRedisMapper userMapper;
    private final FeedRedisRepository feedRedisRepository;

    @Async
    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            KafkaPostEvent event = objectMapper.readValue(record.value(), KafkaPostEvent.class);
                postRedisRepository.save(PostRedis.builder()
                        .postId(event.getPostId())
                        .authorId(event.getAuthor().getId())
                        .build());
                UserRedis userRedis = UserRedis.builder()
                        .followers(event.getAuthor().getFollowersIds())
                        .id(event.getAuthor().getId())
                        .build();
                log.info("UserRedis: {}", userRedis);
                userRedisRepository.save(userRedis);
                List<Long> followersIds = event.getAuthor().getFollowersIds();
                if (followersIds != null) {
                    for (Long followerId : followersIds) {
                        long timestamp = event.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        feedRedisRepository.addPostToFeed(followerId, event.getPostId(), timestamp);
                    }
                }
                log.info("Event: {}", event);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Error processing message", e);
        }
    }
}
