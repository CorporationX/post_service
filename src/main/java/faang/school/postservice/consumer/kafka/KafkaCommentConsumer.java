package faang.school.postservice.consumer.kafka;

import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final RedisCacheService redisCacheService;

    @KafkaListener(topics = "${spring.kafka.topic.comment}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<Long, String> record) {
        Long postId = record.key();
        String commentFeedJson = record.value();

        redisCacheService.addCommentToCache(postId, commentFeedJson);
    }
}
