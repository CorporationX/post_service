package faang.school.postservice.producer.like;

import faang.school.postservice.event.like.KafkaLikeEvent;
import faang.school.postservice.model.Like;
import faang.school.postservice.producer.AbstractProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeProducer extends AbstractProducer<KafkaLikeEvent> implements LikeServiceProducer {

    public LikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${kafka.topic.likes-topic.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }

    @Override
    public void send(Like like) {
        KafkaLikeEvent likeEvent = new KafkaLikeEvent(like.getPost().getId());
        sendEvent(likeEvent);
    }
}
