package faang.school.postservice.kafka;

import faang.school.postservice.service.post.RedisCache;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final RedisCache redisCache;

    @KafkaListener(topics = "${spring.kafka.topic.like}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<Long, String> record) {
        Long postId = record.key();
        String likeJson = record.value();

        redisCache.addLikeToCache(postId, likeJson);
    }
}
