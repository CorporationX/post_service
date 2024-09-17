package faang.school.postservice.messaging.kafka.listener.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.post_views}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message) {
        try {
            PostEvent postEvent = objectMapper.readValue(message.getBytes(), PostEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}