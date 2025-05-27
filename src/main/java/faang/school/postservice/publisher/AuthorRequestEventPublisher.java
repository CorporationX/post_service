package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.AuthorRequestTopicProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthorRequestEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<Long> {

    private final AuthorRequestTopicProperties authorRequestTopic;

    public AuthorRequestEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper,
                                       AuthorRequestTopicProperties authorRequestTopic) {
        super(kafkaTemplate, objectMapper);
        this.authorRequestTopic = authorRequestTopic;
    }

    @Override
    public void publish(Long authorId) {
        sendMessage(authorId, authorRequestTopic.name());
    }
}
