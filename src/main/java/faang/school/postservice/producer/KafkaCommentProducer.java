package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.PostCommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractProducer<PostCommentEvent>
        implements KafkaProducer<PostCommentEvent> {

    @Value("${spring.kafka.topic.comment-post}")
    private String topic;

    public KafkaCommentProducer(KafkaTemplate kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void send(PostCommentEvent event) {
        super.sendMessage(topic, event);
    }
}
