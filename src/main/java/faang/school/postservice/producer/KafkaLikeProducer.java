package faang.school.postservice.producer;

import faang.school.postservice.dto.event.LikeEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer
        extends AbstractKafkaProducer<LikeEventKafka> {

    @Value(value = "${spring.kafka.topics.like}")
    private String topicLike;

    @Async("executor")
    public void sendMessage(LikeEventKafka likeEventKafka) {
        sendMessage(likeEventKafka, topicLike);
    }
}
