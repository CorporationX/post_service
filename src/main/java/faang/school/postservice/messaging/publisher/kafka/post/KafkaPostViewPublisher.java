package faang.school.postservice.messaging.publisher.kafka.post;

import faang.school.postservice.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.messaging.publisher.kafka.AbstractKafkaEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewPublisher extends AbstractKafkaEventPublisher<PostViewKafkaEvent> {
    public KafkaPostViewPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                  NewTopic postViewsTopic) {
        super(kafkaTemplate, postViewsTopic);
    }

    @Override
    public void publish(PostViewKafkaEvent event) {
        super.publish(event);
    }
}