package faang.school.postservice.service.comment;

import faang.school.postservice.model.kafka.KafkaCommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaCommentProducer {

    private final KafkaTemplate<String, KafkaCommentEvent> kafkaTemplate;
    @Qualifier("comment")
    private final NewTopic commentTopic;

    public KafkaCommentProducer(KafkaTemplate<String, KafkaCommentEvent> kafkaTemplate,
                                @Qualifier("comment") NewTopic commentTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.commentTopic = commentTopic;
    }

    public void sendMessage(KafkaCommentEvent commentMessage) {
        String commentTopicName = commentTopic.name();
        log.info("Sending message {} to topic {}.", commentMessage, commentTopic);
        CompletableFuture<SendResult<String, KafkaCommentEvent>> future
                = kafkaTemplate.send(commentTopicName, commentMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message {} was sent to {} topic with result {}",
                        commentMessage, commentTopicName, result.getRecordMetadata().toString());
            } else {
                log.info("Message was not sent due to exception {}.", ex.getMessage());
            }
        });
    }
}
