package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractEventProducer<FeedCommentEvent> {
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${spring.data.kafka.topics.comment.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
