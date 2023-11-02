package faang.school.postservice.service.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends KafkaAbstractProducer {

    @Value("${spring.kafka.topics.comment-topic}")
    private String commentTopicName;

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    @Async("kafkaThreadPool")
    public void sendMessage(KafkaKey key, Object message) {
        var future = kafkaTemplate.send(commentTopicName, key.name(), message);
        handleFuture(future);
    }
}
