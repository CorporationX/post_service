package faang.school.postservice.kafka.producer.comment;

import faang.school.postservice.config.properties.kafka.FeedKafkaTopicsProperties;
import faang.school.postservice.dto.comment.CommentFeedEvent;
import faang.school.postservice.kafka.producer.AbstractKafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedCommentProducer extends AbstractKafkaProducer<CommentFeedEvent> {

    private final FeedKafkaTopicsProperties topics;

    public FeedCommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                               FeedKafkaTopicsProperties topics) {
        super(kafkaTemplate);
        this.topics = topics;
    }

    public void publish(CommentFeedEvent event) {
        String key = String.valueOf(event.postId());
        publishEvent(topics.comment(), key, event);
    }
}
