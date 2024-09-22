package faang.school.postservice.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topicPost;


    public void sendMessage(String msg) {
        String postTopicName = topicPost.name();
        log.info("Sending message {} to topic {}.", msg, postTopicName);
        kafkaTemplate.send(postTopicName, msg);
        log.info("Message was sent.");
    }

    public void sendMessage2(String message) {
        String postTopicName = topicPost.name();
        log.info("Sending message {} to topic {}.", message, topicPost);
        CompletableFuture<SendResult<String, Object>> future
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
