package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.PostsViewTopicProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostsViewEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<Long> {

    private final PostsViewTopicProperties postsViewTopic;

    public PostsViewEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper,
                                   PostsViewTopicProperties postsViewTopicProperties) {
        super(kafkaTemplate, objectMapper);
        this.postsViewTopic = postsViewTopicProperties;
    }

    @Override
    public void publish(Long postId) {
        sendMessage(postId, postsViewTopic.name());
    }
}
