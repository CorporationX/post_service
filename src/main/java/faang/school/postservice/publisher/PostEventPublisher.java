package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.FollowersRequestTopicProperties;
import faang.school.postservice.dto.feed.PostPublishEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<PostPublishEvent> {

    private final FollowersRequestTopicProperties followersRequestTopicProperties;

    public PostEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper,
                              FollowersRequestTopicProperties followersRequestTopicProperties) {
        super(kafkaTemplate, objectMapper);
        this.followersRequestTopicProperties = followersRequestTopicProperties;
    }

    @Override
    public void publish(PostPublishEvent event) {
        sendMessage(event, followersRequestTopicProperties.name());
    }
}
