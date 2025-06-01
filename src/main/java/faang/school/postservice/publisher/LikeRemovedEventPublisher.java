package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.LikeRemovedTopicProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeRemovedEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<Long> {

    private final LikeRemovedTopicProperties likeRemovedTopicProperties;

    public LikeRemovedEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper,
                                     LikeRemovedTopicProperties likeRemovedTopicProperties) {
        super(kafkaTemplate, objectMapper);
        this.likeRemovedTopicProperties = likeRemovedTopicProperties;
    }

    @Override
    public void publish(Long postId) {
        sendMessage(postId, likeRemovedTopicProperties.name());
    }
}
