package faang.school.postservice.producer;

import faang.school.postservice.event.like.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractKafkaMessageProducer<PostViewEvent> {

    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                 NewTopic postViewTopic) {
        super(kafkaTemplate, postViewTopic);
    }
}
