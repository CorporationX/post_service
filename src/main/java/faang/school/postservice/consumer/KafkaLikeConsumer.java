package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.events.KafkaLikeEvent;
import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.repository.post.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private static final String TOPIC = "${spring.kafka1.topics.publish-post.name}";
    private static final String GROUP_ID = "${spring.kafka1.consumer.group-id}";

    private final ObjectMapper objectMapper;
    private final PostRedisRepository redisPostRepository;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            KafkaLikeEvent event = objectMapper.readValue(record.value(), KafkaLikeEvent.class);
            log.info("Event: {}", event);
            if (running.compareAndSet(false, true) ) {
                redisTemplate.watch(event.getPostId().toString());
                redisTemplate.multi();

                PostRedis postRedis = redisPostRepository.findById(event.getPostId().toString()).orElseThrow(NullPointerException::new);
                postRedis.setCountLikes(postRedis.getCountLikes() + 1);
                redisPostRepository.save(postRedis);
                running.set(false);
            }
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Error processing message", e);
        }
    }
}
