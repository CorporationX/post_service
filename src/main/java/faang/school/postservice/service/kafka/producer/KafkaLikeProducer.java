package faang.school.postservice.service.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends KafkaAbstractProducer {

    @Value("${spring.kafka.topics.like-topic}")
    private String likeTopicName;

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    @Async("kafkaThreadPool")
    public void sendMessage(KafkaKey key, Object message) {
        var future = kafkaTemplate.send(likeTopicName, key.name(), message);
        handleFuture(future);
    }
}
