package faang.school.postservice.listener;

import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.repository.FeedRedisRepository;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import static faang.school.postservice.contants.ErrorMessage.FAILED_TO_PROCESS_EVENT;

@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final JsonUtils jsonUtils;
    private final FeedRedisRepository feedRedisRepository;

    @Retryable(backoff = @Backoff(delay = 100))
    @KafkaListener(topics = "${spring.kafka.producer.topics.post}",
            groupId = "${spring.kafka.consumer.group-id.post}")
    public void listen(String message, Acknowledgment ack) {
        try {
            PostEvent event = jsonUtils.fromJson(message, PostEvent.class);
            log.info("Post event received: {}", event);
            event.getFollowers().forEach(follower -> feedRedisRepository.save(follower, event.getPostId()));
            ack.acknowledge();
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