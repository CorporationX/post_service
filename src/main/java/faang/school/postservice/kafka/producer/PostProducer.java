package faang.school.postservice.kafka.producer;

import faang.school.postservice.config.kafka.KafkaTopicConfig;
import faang.school.postservice.dto.post.PostDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostProducer extends AbstractKafkaProducer<PostDto> {

    private final KafkaTopicConfig topic;

    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                           KafkaTopicConfig topic) {
        super(kafkaTemplate);
        this.topic = topic;
    }

    public void publishPost(PostDto event) {
        publishEvent(topic.postCreateTopic().name(), String.valueOf(event.id()), event);
    }
}