package faang.school.postservice.consumer.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.ViewsEvent;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewsConsumer {
    private final PostCacheRepository postCacheRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "views", groupId = "view-group")
    public void consume(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        ViewsEvent event = objectMapper.readValue(message, ViewsEvent.class);

        try {
            postCacheRepository.incrementView(event.getPostId());
            acknowledgment.acknowledge();
            log.info("Successfully add like to post {} and acknowledged message", event.getPostId());
        } catch (Exception e) {
            log.error("Failed to add like to post {}: {}", event.getPostId(), e.getMessage());
            e.printStackTrace();
        }
    }
}
