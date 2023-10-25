package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.PostEventKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaPostProducer extends AbstractProducer<PostEventKafka> {
    @Value("${spring.kafka.channels.posts_publisher.name}")
    private String topic;
    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void publish(PostEventKafka event) {
       CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);
       future.whenComplete((ack, exception) -> {
           if (exception != null) {
               log.error("Failed to send message", exception);
           } else {
               log.info("Message sent successfully");
           }
       });
    }
}
