package faang.school.postservice.publisher.kafka_producer;

import faang.school.postservice.dto.kafka_events.LikeKafkaEvent;
import faang.school.postservice.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaLikeProducer extends AbstractKafkaProducer<LikeKafkaEvent> {
    @Value("${spring.kafka.topics.like.name}")
    private String likeTopic;

    public void publishLikeKafkaEvent(Like like) {
        LikeKafkaEvent likeKafkaEvent = new LikeKafkaEvent(like);
        publishKafkaEvent(likeKafkaEvent, likeTopic);
    }

}
