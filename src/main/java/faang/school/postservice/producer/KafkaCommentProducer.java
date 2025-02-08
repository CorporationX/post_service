package faang.school.postservice.producer;

import faang.school.postservice.model.event.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends KafkaAbstractProducer<CommentEvent> {

    @Value("${spring.data.kafka.topics.comment_topic}")
    private String commentTopic;

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Bean
    public NewTopic commentTopic() {
        return TopicBuilder.name(commentTopic).build();
    }

    public void send(CommentEvent comment) {
        super.sendMessage(commentTopic, comment);
    }
}
