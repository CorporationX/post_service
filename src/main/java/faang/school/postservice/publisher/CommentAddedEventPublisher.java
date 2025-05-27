package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.CommentAddedTopicProperties;
import faang.school.postservice.dto.feed.CommentAddedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentAddedEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<CommentAddedEvent> {

    private final CommentAddedTopicProperties commentTopicProperties;

    public CommentAddedEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                      ObjectMapper objectMapper,
                                      CommentAddedTopicProperties commentTopicProperties) {
         super(kafkaTemplate, objectMapper);
         this.commentTopicProperties = commentTopicProperties;
    }

    @Override
    public void publish(CommentAddedEvent event) {
        sendMessage(event, commentTopicProperties.name());
    }

}
