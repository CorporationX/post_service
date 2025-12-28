package faang.school.postservice.listener;

import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventListener {
    final PostCacheRepository postCacheRepository;

    @KafkaListener(
            topics = "${kafka.topic.like-event}",
            containerFactory = "concurrentKafkaLikeListenerFactory"
    )
    public void handleLikeSetEvent(LikeEventDto likeEventDto, Acknowledgment acknowledgment) {
        log.info("New like set event: {}", likeEventDto);
        try {
            postCacheRepository.incrementLikeCount(likeEventDto.postId());
        } catch (Exception exception) {
            log.error("Failed to process a like event: {}", likeEventDto, exception);
            return;
        }
        acknowledgment.acknowledge();
    }
}
