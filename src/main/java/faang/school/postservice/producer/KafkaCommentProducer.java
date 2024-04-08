package faang.school.postservice.producer;

import faang.school.postservice.dto.event.CommentEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractEventProducer<CommentEventKafka>{
    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;
    @Value("${spring.kafka.topics.comment.cache}")
    private String topicCacheComment;

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }


    public void publish(CommentEventKafka commentEventKafka) {
        sendMessage(commentEventKafka, commentTopic);
    }

    public void publishHeatCache(CommentEventKafka commentEventKafka) {
        sendMessage(commentEventKafka, topicCacheComment);
    }

}
