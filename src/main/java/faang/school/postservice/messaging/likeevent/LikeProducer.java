package faang.school.postservice.messaging.likeevent;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeProducer {
    @Value("${spring.kafka.topics.like-publication}")
    private String likePublicationTopic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(LikeDto likeDto) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(likePublicationTopic, likeDto);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Event was sent: {}", result);
            } else {
                log.error("Failed to send event: {}", e.getMessage());
            }
        });
    }
}
