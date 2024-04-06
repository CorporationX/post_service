package faang.school.postservice.producer;

import faang.school.postservice.dto.event.LikeEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractEventProducer<LikeEventKafka>{

    @Value("${spring.kafka.topics.like.name}")
    private String topicLike;

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish(LikeEventKafka likeEventKafka) {
        sendMessage(likeEventKafka, topicLike);
    }
}
