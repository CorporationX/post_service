package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostKafkaEvent>{

    private final NewTopic posts;

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic posts) {
        super(kafkaTemplate);
        this.posts = posts;
    }

    public void sendEvent(PostKafkaEvent event) {
        super.sendEvent(event, posts);
    }
}
