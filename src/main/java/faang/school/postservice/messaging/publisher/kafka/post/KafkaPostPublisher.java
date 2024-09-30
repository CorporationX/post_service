package faang.school.postservice.messaging.publisher.kafka.post;

import faang.school.postservice.event.kafka.PostKafkaEvent;
import faang.school.postservice.messaging.publisher.kafka.AbstractKafkaEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostPublisher extends AbstractKafkaEventPublisher<PostKafkaEvent> {
    public KafkaPostPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                              NewTopic postsTopic) {
        super(kafkaTemplate, postsTopic);
    }

    @Override
    public void publish(PostKafkaEvent event) {
        super.publish(event);
    }
}