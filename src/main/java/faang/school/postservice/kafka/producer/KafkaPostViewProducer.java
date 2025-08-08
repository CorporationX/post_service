package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.ViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaPostViewProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.producer.topics.post-views}")
    private String postViewTopic;

    public void sendViewEvent(Long postId, Long userId) {
        ViewEvent viewEvent = createNewViewEvent(postId, userId);
        log.debug("Sending view postId = {}, userId={}", postId, userId);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                postViewTopic,
                String.valueOf(postId),
                viewEvent
        );

        future.whenComplete((res, ex) -> {
            if (ex == null) {
                var md = res.getRecordMetadata();
                log.debug(
                        "View event sent: topic={}, partition={}, offset={}, postId={}, userId={}",
                        md.topic(), md.partition(), md.offset(), postId, userId
                );
            } else {
                log.error(
                        "Error during view event: postId={}, userId={}",
                        postId, userId, ex
                );
            }
        });

    }

    private ViewEvent createNewViewEvent(Long postId, Long userId) {
        ViewEvent viewEvent = new ViewEvent();
        viewEvent.setId(UUID.randomUUID());
        viewEvent.setPostId(postId);
        viewEvent.setUserId(userId);
        viewEvent.setTimestamp(LocalDateTime.now());
        return viewEvent;

    }
}
