package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.PostEventKafka;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Data
@RequiredArgsConstructor
public class PostEventConsumer {
    private final RedisFeedRepository redisFeedRepository;

    @KafkaListener(topics = "posts-topic", groupId = "test-consumer-group")
    public PostEventKafka consume(PostEventKafka message) {
        log.info("Consumed message" + message);
        List<Long> followersIds = message.getFollowersIds();
        followersIds.forEach(id -> {

//            redisFeedRepository.save(FeedForRedis.builder().id(id).postIds().build());
        });

        return message;
    }
}
