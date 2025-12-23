package faang.school.postservice.comsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.PostForFeedDto;
import faang.school.postservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostFeedListener {

    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topics.post-feed}",
            containerFactory = "postFeedKafkaListenerContainerFactory",
            groupId = "post-feed"
    )
    public void kafkaPostsFeedListener(@Payload Map<String, Object> message, Acknowledgment ack) {
        PostForFeedDto postForFeedDto = objectMapper.convertValue(message, PostForFeedDto.class);
        log.info("Received post for feed: {}", postForFeedDto);

        redisService.savePostForFeed(postForFeedDto);

        ack.acknowledge();

        log.info("Successfully saved post for feed and ack message: {}", postForFeedDto);
    }
}
