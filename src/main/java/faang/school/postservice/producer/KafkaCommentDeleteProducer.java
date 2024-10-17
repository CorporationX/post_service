package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentDeleteEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentDeleteProducer extends AbstractEventProducer<FeedCommentDeleteEvent> {
    public KafkaCommentDeleteProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                      @Value("${spring.data.kafka.topics.delete-comment.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
