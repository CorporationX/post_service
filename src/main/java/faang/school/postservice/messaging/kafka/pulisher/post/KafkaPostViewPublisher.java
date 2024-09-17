package faang.school.postservice.messaging.kafka.pulisher.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.messaging.kafka.pulisher.AbstractKafkaPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewPublisher extends AbstractKafkaPublisher<PostEvent> {

    public KafkaPostViewPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${spring.kafka.topic.post_views}") String postViewsTopicName) {
        super(kafkaTemplate, objectMapper, postViewsTopicName);
    }

    @Override
    public void publish(PostEvent event) {
        super.publish(event);
    }
}