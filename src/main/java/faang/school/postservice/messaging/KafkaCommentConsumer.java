package faang.school.postservice.messaging;

import faang.school.postservice.mapper.JsonMapper;
import faang.school.postservice.model.redis.CachedComment;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final JsonMapper mapper;
    private final RedisPostRepository redisPostRepository;


    @KafkaListener(topics = "${spring.data.kafka.topic.comments}")
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void listen(String json, Acknowledgment acknowledgment) {
        try {
            CachedComment event = mapper.mapToObject(json, CachedComment.class);
            redisPostRepository.addComment(event.getPostId(), event);
            acknowledgment.acknowledge();
            log.info("Comment with id: {} for post with id: {} was cached", event.getId(), event.getPostId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
