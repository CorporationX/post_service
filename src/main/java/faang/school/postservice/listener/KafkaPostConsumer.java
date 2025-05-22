package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.repository.FeedRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import static faang.school.postservice.contants.ErrorMessage.FAILED_SERIALIZING_OBJECT;
import static faang.school.postservice.contants.ErrorMessage.FAILED_TO_PROCESS_EVENT;

@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final ObjectMapper objectMapper;
    private final FeedRedisRepository feedRedisRepository;

    @Retryable(backoff = @Backoff(delay = 100))
    @KafkaListener(topics = "${spring.kafka.producer.topics.post}",
            groupId = "${spring.kafka.consumer.group-id.post}")
    public void listen(String message, Acknowledgment ack) {
        try {
            PostEvent event = objectMapper.readValue(message, PostEvent.class);
            log.info("Post event received: {}", event);
            event.getFollowers().forEach(follower -> feedRedisRepository.addToFeed(follower, event.getPostId()));
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(FAILED_SERIALIZING_OBJECT, e);
            throw new RuntimeException(FAILED_SERIALIZING_OBJECT);
        } catch (Exception e) {
            log.error(FAILED_TO_PROCESS_EVENT);
            throw e;
        }
    }

    @Recover
    public void recover(Acknowledgment ack) {
        ack.acknowledge();
    }
}