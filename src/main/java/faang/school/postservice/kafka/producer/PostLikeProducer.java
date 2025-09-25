package faang.school.postservice.kafka.producer;

import faang.school.postservice.config.kafka.KafkaTopicConfig;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.model.Like;
import org.springframework.kafka.core.KafkaTemplate;

public class PostLikeProducer extends AbstractKafkaProducer<Like> {

    private final KafkaTopicConfig topic;

    public PostLikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                           KafkaTopicConfig topic) {
        super(kafkaTemplate);
        this.topic = topic;
    }

    public void publishPostLikeEvent(Like event) {
        publishEvent(topic.postCommentPublishedTopic().name(), String.valueOf(event.getId()), event);
    }
}