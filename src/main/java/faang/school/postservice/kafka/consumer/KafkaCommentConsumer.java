package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.service.redis.dto.CommentCacheDto;
import faang.school.postservice.service.redis.entity.PostRedis;
import faang.school.postservice.service.redis.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private final PostRedisRepository postRedisRepository;
    private final ObjectMapper objectMapper;
    private final CommentEventMapper commentEventMapper;

    @Value("${app.redis.post.max-comments}")
    private int maxCommentsInCache;

    @Value("${app.redis.post.retry}")
    private int retries;

    @KafkaListener(
            topics = "comment-event-topic",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCommentEvent(CommentEvent event, Acknowledgment ack) {
        CommentCacheDto dto = commentEventMapper.createDtoFromEvent(event);

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                Optional<PostRedis> optional = postRedisRepository.findById(event.getPostId());

                if (optional.isEmpty()) {
                    log.warn("Post with ID {} not found in cache. Skipping.", event.getPostId());
                    ack.acknowledge();
                    return;
                }
                PostRedis postRedis = optional.get();

                postRedis.getLastComments().add(dto);

                while (postRedis.getLastComments().size() > maxCommentsInCache) {
                    postRedis.getLastComments().pollFirst();
                }

                postRedisRepository.save(postRedis);

                log.info("Successfully processed comment for post {}", event.getPostId());
                ack.acknowledge();
                return;
            } catch (OptimisticLockingFailureException ex) {
                log.warn("Optimistic lock conflict for post {}. Attempt {}/{}", event.getPostId(), attempt, retries);
                if (attempt == retries) {
                    throw ex;
                }
            }
        }
    }
}
