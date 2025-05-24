package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feed.PostPublishEvent;
import faang.school.postservice.exception.JsonDeserializationException;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.posts.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receive(String message) {
        try {
            log.debug("Received new post publish event: {}", message);
            PostPublishEvent event = objectMapper.readValue(message, PostPublishEvent.class);
            //feedService.someMethod(event);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException("Deserialization json %s to event object error", message);
        }
    }
}
