package faang.school.postservice.kafka.producer.like;

import faang.school.postservice.config.properties.kafka.FeedKafkaTopicsProperties;
import faang.school.postservice.dto.like.LikeFeedEvent;
import faang.school.postservice.kafka.producer.AbstractKafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeFeedProducer extends AbstractKafkaProducer<LikeFeedEvent> {

    private final FeedKafkaTopicsProperties topics;

    public LikeFeedProducer(KafkaTemplate<String, Object> kafkaTemplate,
                            FeedKafkaTopicsProperties topics) {
        super(kafkaTemplate);
        this.topics = topics;
    }

    public void publish(LikeFeedEvent event) {
        publishEvent(topics.like(), String.valueOf(event.postId()), event);
    }
}
