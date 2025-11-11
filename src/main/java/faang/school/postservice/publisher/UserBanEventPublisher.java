package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.UserBanEvent;
import faang.school.postservice.exception.EventPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.user-ban}")
    private String userBanTopic;

    public void publish(UserBanEvent userBanEvent) {
        try {
            String jsonBody = objectMapper.writeValueAsString(userBanEvent);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(userBanTopic, jsonBody);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send user ban event for users {}", userBanEvent.userIds());
                } else {
                    log.info("Sent user ban event for users {}", userBanEvent.userIds());
                }
            });
        } catch (JsonProcessingException e) {
            String errorMessage = "JSON serialization failed for user ban event";
            log.error(errorMessage);
            throw new EventPublishingException(errorMessage);
        }
    }
}