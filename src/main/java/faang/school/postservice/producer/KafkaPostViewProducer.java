package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostViewEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractEventProducer<PostViewEventKafka>{
    @Value("${spring.kafka.topics.post_view.name}")
    private String postViewTopic;

    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish(PostViewEventKafka postViewEventKafka) {
        sendMessage(postViewEventKafka, postViewTopic);
    }
}
