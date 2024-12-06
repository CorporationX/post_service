package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.events.KafkaCommentEvent;
import faang.school.postservice.dto.kafka.events.KafkaLikeEvent;
import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.repository.post.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private static final String TOPIC = "${spring.kafka1.topics.comments.name}";
    private static final String GROUP_ID = "${spring.kafka1.consumer.group-id}";

    private final ObjectMapper objectMapper;
    private final PostRedisRepository postRedisRepository;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            KafkaCommentEvent event = objectMapper.readValue(record.value(), KafkaCommentEvent.class);
            log.info("Event: {}", event);
            if (running.compareAndSet(false, true)) {
                PostRedis postRedis = postRedisRepository.findById(event.getPostId().toString()).orElseThrow(NullPointerException::new);
                postRedis.setCountComments(postRedis.getCountComments() + 1);
                postRedisRepository.save(postRedis);
                running.set(false);
            }
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Error processing message", e);
        }
    }
}
