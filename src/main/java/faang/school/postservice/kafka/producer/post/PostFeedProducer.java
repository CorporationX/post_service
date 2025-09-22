package faang.school.postservice.kafka.producer.post;

import faang.school.postservice.config.properties.kafka.FeedKafkaTopicsProperties;
import faang.school.postservice.dto.post.PostFeedEvent;
import faang.school.postservice.kafka.producer.AbstractKafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostFeedProducer extends AbstractKafkaProducer<PostFeedEvent> {

    private final FeedKafkaTopicsProperties topics;

    public PostFeedProducer(KafkaTemplate<String, Object> kafkaTemplate,
                            FeedKafkaTopicsProperties topics) {
        super(kafkaTemplate);
        this.topics = topics;
    }

    public void publishPostCreated(PostFeedEvent event) {
        publishEvent(topics.post(), String.valueOf(event.authorId()), event);
    }
}
