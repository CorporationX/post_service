package faang.school.postservice.messaging;

import faang.school.postservice.mapper.JsonMapper;
import faang.school.postservice.model.redis.CachedLike;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final JsonMapper mapper;
    private final RedisPostRepository redisPostRepository;


    @KafkaListener(topics = "${spring.data.kafka.topic.likes}")
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void listen(String json, Acknowledgment acknowledgment) {
        try {
            CachedLike event = mapper.mapToObject(json, CachedLike.class);
            redisPostRepository.addLike(event.getPostId(), event);
            acknowledgment.acknowledge();
            log.info("Like with id: {} for post with id: {} was cached", event.getId(), event.getPostId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
