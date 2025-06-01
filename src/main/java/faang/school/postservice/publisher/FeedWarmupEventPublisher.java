package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.FeedWarmupTopicProperties;
import faang.school.postservice.dto.feed.FeedWarmupBatchEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedWarmupEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<FeedWarmupBatchEvent> {

    private final FeedWarmupTopicProperties feedWarmupTopicProperties;

    public FeedWarmupEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                    ObjectMapper objectMapper,
                                    FeedWarmupTopicProperties feedWarmupTopicProperties) {
        super(kafkaTemplate, objectMapper);
        this.feedWarmupTopicProperties = feedWarmupTopicProperties;
    }

    @Override
    public void publish(FeedWarmupBatchEvent event) {
        sendMessage(event, feedWarmupTopicProperties.name());
    }
}
