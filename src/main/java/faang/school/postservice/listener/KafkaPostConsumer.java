package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final ObjectMapper mapper;
    private final RedisFeedRepository redisFeedRepository;

    @KafkaListener(topics = "${spring.data.kafka.topic.post_published}")
    public void listen(String json) {
        try {
            PostPublishedEvent event = mapper.readValue(json, PostPublishedEvent.class);
            redisFeedRepository.updateFeed(event);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
