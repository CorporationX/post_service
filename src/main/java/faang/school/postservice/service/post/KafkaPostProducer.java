package faang.school.postservice.service.post;

import faang.school.postservice.model.kafka.KafkaPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class KafkaPostProducer {

    private final KafkaTemplate<String, KafkaPostEvent> kafkaTemplate;
    @Qualifier("post")
    private final NewTopic topicPost;

    public KafkaPostProducer(KafkaTemplate<String, KafkaPostEvent> kafkaTemplate,
                             @Qualifier("post") NewTopic topicPost) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicPost = topicPost;
    }

    public void sendMessage(KafkaPostEvent msg) {
        String postTopicName = topicPost.name();
        log.info("Sending message {} to topic {}.", msg, postTopicName);
        kafkaTemplate.send(postTopicName, msg);
        log.info("Message was sent.");
    }

    public void sendMessage2(KafkaPostEvent message) {
        String postTopicName = topicPost.name();
        log.info("Sending message {} to topic {}.", message, topicPost);
        CompletableFuture<SendResult<String, KafkaPostEvent>> future
                = kafkaTemplate.send(postTopicName, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message {} was sent to {} topic with result {}",
                        message, postTopicName, result.getRecordMetadata().toString());
            } else {
                log.info("Message was not sent due to exception {}.", ex.getMessage());
            }
        });
    }
}
