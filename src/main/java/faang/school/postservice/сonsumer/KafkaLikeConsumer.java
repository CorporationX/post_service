package faang.school.postservice.сonsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.properties.FeedCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final FeedCacheProperties feedCacheProperties;


    @KafkaListener(topics = "likes", groupId = "post-service-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            LikeEventDto likeEventDto = objectMapper.readValue(record.value(), LikeEventDto.class);
            String key = "post:" + likeEventDto.getPostId();

            PostRedisDto postRedisDto = (PostRedisDto) redisTemplate.opsForValue().get(key);

            if (postRedisDto == null) {
                log.warn("Пост {} не найден в Redis (возможно, устарел)", likeEventDto.getPostId());
                return;
            }

            postRedisDto.setLikeCount(postRedisDto.getLikeCount() + 1);
            redisTemplate.opsForValue().set(key, postRedisDto, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));

        } catch (Exception e) {
            log.error("Ошибка обработки KafkaLikeConsumer", e);
        }
    }
}
