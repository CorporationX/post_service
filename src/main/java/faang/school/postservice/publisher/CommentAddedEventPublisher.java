package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.CommentAddedTopicProperties;
import faang.school.postservice.dto.feed.CommentRedisEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentAddedEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<CommentRedisEvent> {

    private final CommentAddedTopicProperties commentTopicProperties;

    public CommentAddedEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                      ObjectMapper objectMapper,
                                      CommentAddedTopicProperties commentTopicProperties) {
         super(kafkaTemplate, objectMapper);
         this.commentTopicProperties = commentTopicProperties;
    }

    @Override
    public void publish(CommentRedisEvent event) {
        sendMessage(event, commentTopicProperties.name());
    }

}
