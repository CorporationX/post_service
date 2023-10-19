package faang.school.postservice.messaging.likeevent;

import faang.school.postservice.dto.like.LikeDto;
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
public class LikeEventConsumer {
    private final KafkaTemplate<String, LikeDto> kafkaTemplate;

    @Value("${spring.kafka.topics.like-publication}")
    private String likePublicationTopic;

    public void publish(LikeDto likeDto) {
        CompletableFuture<SendResult<String, LikeDto>> future = kafkaTemplate.send(likePublicationTopic, likeDto);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Event was sent: {}", result);
            } else {
                log.error("Failed to send event: {}", e.getMessage());
            }
        });
    }
}
