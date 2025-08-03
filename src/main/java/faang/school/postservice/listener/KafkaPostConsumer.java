package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.publisher.post.PostEventPublisher;
import faang.school.postservice.service.cache.CachePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {

    private final ObjectMapper objectMapper;
    private final CachePostService cachePostService;


    @KafkaListener(topics = PostEventPublisher.KAFKA_TOPIC, groupId = "CachePost")
    public void listen(String message) {
        try {
            PostEvent postEvent = objectMapper.readValue(message, PostEvent.class);
            log.info("Received PostEvent: {}", postEvent);
            cachePostService.savePost(postEvent);
        } catch (JsonProcessingException exception) {
            log.error("Error deserializing PostEvent", exception);
        }
    }
}
