package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostPublishingEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostPublishingEventPublisher extends AbstractEventPublisher<PostPublishingEvent> {
    public PostPublishingEventPublisher(
            @Value("${spring.kafka.topics.news-feed.posts.name}") String topic,
            KafkaTemplate<String, PostPublishingEvent> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}
