package faang.school.postservice.listener;

import faang.school.postservice.dto.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Bulgakov
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.feed-limit}")
    private int feedLimit;
    @Value("${spring.data.redis.stores.keys.feed}")
    private String partKeyStore;

    @KafkaListener(topics = "${spring.kafka.topics.posts}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostCreatedEvent postCreatedEvent, Acknowledgment acknowledgment) {
        try {
            processEvent(postCreatedEvent);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения Kafka: {}", e.getMessage(), e);
        }
    }

    private void processEvent(PostCreatedEvent postCreatedEvent) {
        Long postId = postCreatedEvent.postId();
        Set<Long> subscriberIds = new HashSet<>(postCreatedEvent.subscriberIds());
        long timestamp = postCreatedEvent.createdAt().toEpochSecond(ZoneOffset.UTC);

        subscriberIds.forEach(subscriberId -> {
            String key = partKeyStore + subscriberId;
            ZSetOperations<String, Object> feed = redisTemplate.opsForZSet();
            // отрицательное значение временной метки нужно для инвертирования порядка сортировки
            feed.add(key, postId, -timestamp);

            ensureFeedLimit(feed, key, feedLimit);
        });
    }

    private void ensureFeedLimit(ZSetOperations<String, Object> feed, String key, int limit) {
        Long size = feed.size(key);
        if (size != null && size > limit) {
            feed.removeRange(key, 0, size - limit - 1);
        }
    }
}
