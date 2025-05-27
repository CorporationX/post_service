package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.LikeAddedTopicProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeAddedEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<Long> {

    private final LikeAddedTopicProperties likeTopicProperties;

    public LikeAddedEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper,
                                   LikeAddedTopicProperties likeTopicProperties) {
        super(kafkaTemplate, objectMapper);
        this.likeTopicProperties = likeTopicProperties;
    }

    @Override
    public void publish(Long postId) {
        sendMessage(postId, likeTopicProperties.name());
    }
}
