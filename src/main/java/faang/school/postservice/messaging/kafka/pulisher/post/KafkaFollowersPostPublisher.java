package faang.school.postservice.messaging.kafka.pulisher.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.FollowersPostEvent;
import faang.school.postservice.messaging.kafka.pulisher.AbstractKafkaPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaFollowersPostPublisher extends AbstractKafkaPublisher<FollowersPostEvent> {

    @Autowired
    public KafkaFollowersPostPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper,
                                       @Value("${spring.kafka.topic.posts}") String followersPostTopicName) {
        super(kafkaTemplate, objectMapper, followersPostTopicName);
    }

    @Override
    public void publish(FollowersPostEvent event) {
        super.publish(event);
    }
}