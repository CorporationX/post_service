package faang.school.postservice.kafka.producer.post;

import faang.school.postservice.config.properties.kafka.KafkaTopicsProperties;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.kafka.producer.AbstractKafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class PostProducer extends AbstractKafkaProducer<PostPublishedEvent> {

    private final KafkaTopicsProperties topics;

    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        KafkaTopicsProperties topics) {
        super(kafkaTemplate);
        this.topics = topics;
    }

    public void publishPostPublishedEvent(PostPublishedEvent event) {
        publishEvent(topics.post_published(), String.valueOf(event.getPostId()), event);
    }
}
