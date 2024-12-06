package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.events.KafkaPostEvent;
import faang.school.postservice.dto.kafka.events.KafkaPostViewEvent;
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
public class KafkaPostViewConsumer {
    private static final String TOPIC = "${spring.kafka1.topics.post-view.name}";
    private static final String GROUP_ID = "${spring.kafka1.consumer.group-id}";

    private final ObjectMapper objectMapper;
    private final PostRedisRepository postRedisRepository;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            KafkaPostViewEvent event = objectMapper.readValue(record.value(), KafkaPostViewEvent.class);
            log.info("Event: {}", event);
            if (running.compareAndSet(false, true)) {
                PostRedis postRedis = postRedisRepository.findById(event.getPostId().toString()).orElseThrow(NullPointerException::new);
                postRedis.setCountViews(postRedis.getCountViews() + 1);
                postRedisRepository.save(postRedis);
                running.set(false);
            }
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Error processing message", e);
        }
    }
}
