package faang.school.postservice.producers;

import faang.school.postservice.dto.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaLikesProducer extends AbstractKafkaProducer<LikeEvent> {

    @Value("${spring.data.kafka.topics.likes.name}")
    private String likesTopicName;

    public KafkaLikesProducer(KafkaTemplate<String, Object> kafkaTemplate){
        super(kafkaTemplate);
    }

    public void sendEvent(LikeEvent event) {
        super.sendEvent(event, likesTopicName);
    }
}