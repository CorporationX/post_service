package faang.school.postservice.producer;

import faang.school.postservice.dto.event.LikePostEvent;
import faang.school.postservice.dto.event.kafka.PostLikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaLikeProducer extends AbstractProducer<PostLikeEvent> {

    @Value("${spring.kafka.topic.like-post}")
    private String topicName;

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendMessage(PostLikeEvent event) {
        super.sendMessage(topicName, event);
    }
}
