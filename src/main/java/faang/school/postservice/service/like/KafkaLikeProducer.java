package faang.school.postservice.service.like;

import faang.school.postservice.model.kafka.KafkaLikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaLikeProducer {

    private final KafkaTemplate<String, KafkaLikeEvent> kafkaTemplate;
    @Qualifier("like")
    private final NewTopic likeTopic;

    public KafkaLikeProducer(KafkaTemplate<String, KafkaLikeEvent> kafkaTemplate,
                             @Qualifier("like") NewTopic likeTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.likeTopic = likeTopic;
    }

    public void sendMessage(KafkaLikeEvent message) {
        log.info("Sending message {} to topic {}.", message, likeTopic.name());
        CompletableFuture<SendResult<String, KafkaLikeEvent>> future
                = kafkaTemplate.send(likeTopic.name(), message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message {} was sent to {} topic with result {}",
                        message, likeTopic, result.getRecordMetadata().toString());
            } else {
                log.info("Message was not sent due to exception {}.", ex.getMessage());
            }
        });
    }
}
