package faang.school.postservice.publisher;

import faang.school.postservice.event.UserBanEvent;
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

    private final KafkaTemplate<String, UserBanEvent> kafkaTemplate;

    @Value("${kafka.topic.user-ban}")
    private String userBanTopic;

    public void publish(UserBanEvent userBanEvent) {
        CompletableFuture<SendResult<String, UserBanEvent>> future = kafkaTemplate.send(userBanTopic, userBanEvent);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send user ban event for users {}", userBanEvent.userIds());
            } else {
                log.info("Sent user ban event for users {}", userBanEvent.userIds());
            }
        });
    }
}