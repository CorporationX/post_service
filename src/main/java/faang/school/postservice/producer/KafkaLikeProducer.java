package faang.school.postservice.producer;

import faang.school.postservice.event.like.PostLikeEvent;
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
public class KafkaLikeProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.likes.name}")
    private String topic;

    public void publish(PostLikeEvent event) {
        log.info("Starting event sending: {}...", event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send event {} to topic: {}", event, topic, ex);
            } else {
                log.info("Event {} sent to topic: {}, partition: {}, offset: {}",
                        event,
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }
}
