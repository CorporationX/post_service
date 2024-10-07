package faang.school.postservice.consumer.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.like.KafkaLikeEvent;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeConsumer {

    private final PostCacheRepository postCacheRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "likes", groupId = "like-group")
    public void consume(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        KafkaLikeEvent likeEvent = objectMapper.readValue(message, KafkaLikeEvent.class);

        try {
            postCacheRepository.incrementLike(likeEvent.getPostId());
            acknowledgment.acknowledge();
            log.info("Successfully add like to post {} and acknowledged message", likeEvent.getPostId());
        } catch (Exception e) {
            log.error("Failed to add like to post {}: {}", likeEvent.getPostId(), e.getMessage());
            e.printStackTrace();
        }
    }
}
