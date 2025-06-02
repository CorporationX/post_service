package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.service.like.RedisLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static faang.school.postservice.contants.ErrorMessage.ERROR_PROCESSING_LIKE_EVENT;
import static faang.school.postservice.contants.ErrorMessage.WARN_POST_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final RedisLikeService redisLikeService;

    @KafkaListener(topics = "${spring.kafka.producer.topics.like}", groupId = "like-consumer-group", concurrency = "3")
    public void consumeLikeEvent(LikeEvent event) {
        log.info("Received like for post {} (author: {})", event.getPostId(), event.getAuthorId());
        try {
            redisLikeService.incrementLikesForPost(event.getPostId());
        } catch (PostNotFoundException e) {
            log.warn(String.format(WARN_POST_NOT_FOUND, event.getPostId()));
        } catch (RuntimeException e) {
            log.error(String.format(ERROR_PROCESSING_LIKE_EVENT, event.getPostId(), e.getMessage()), e);
        }
    }
}
