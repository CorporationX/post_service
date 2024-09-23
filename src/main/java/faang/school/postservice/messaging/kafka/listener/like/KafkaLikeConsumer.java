package faang.school.postservice.messaging.kafka.listener.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.likes}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message) {
        try {
            LikeEvent likeEvent = objectMapper.readValue(message.getBytes(), LikeEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}