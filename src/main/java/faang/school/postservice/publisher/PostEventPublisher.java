package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.PostsTopicProperties;
import faang.school.postservice.dto.feed.PostPublishEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<PostPublishEvent> {

    private final PostsTopicProperties postsTopicProperties;

    public PostEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper,
                              PostsTopicProperties postsTopicProperties) {
        super(kafkaTemplate, objectMapper);
        this.postsTopicProperties = postsTopicProperties;
    }

    @Override
    public void publish(PostPublishEvent event) {
        sendMessage(event, postsTopicProperties.name());
    }
}
