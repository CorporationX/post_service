package faang.school.postservice.kafka.producer;

import faang.school.postservice.config.kafka.KafkaTopicConfig;
import faang.school.postservice.config.properties.kafka.KafkaTopicsProperties;
import faang.school.postservice.dto.comment.CommentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class CommentProducer extends AbstractKafkaProducer<CommentEvent>{

    private final KafkaTopicConfig topic;

    public CommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                           KafkaTopicConfig topic) {
        super(kafkaTemplate);
        this.topic = topic;
    }


    public void publishCommentEvent(CommentEvent event) {
        publishEvent(topic.postCommentPublishedTopic().name(), String.valueOf(event.postId()), event);
    }
}
